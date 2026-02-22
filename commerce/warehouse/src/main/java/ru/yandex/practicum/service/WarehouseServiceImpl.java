package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.DTO.warehouse.*;
import ru.yandex.practicum.exception.order.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.*;
import ru.yandex.practicum.interfaces.OrderDeliveryRepository;
import ru.yandex.practicum.interfaces.WarehouseRepository;
import ru.yandex.practicum.interfaces.WarehouseService;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.OrderDelivery;
import ru.yandex.practicum.model.ProductOfWarehouse;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository repository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final WarehouseMapper mapper;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];


    @Override
    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest newProduct) {

        if (repository.existsById(newProduct.getProductId())) {
            throw new ProductAlreadyExistInWarehouseException("Продукт уже существует.");
        }

        repository.save(mapper.toEntity(newProduct));
        log.info("Продукт с ID: {} успешно добавлен на склад.", newProduct.getProductId());
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnough(ShoppingCartDto shoppingCartDto) {

        if (shoppingCartDto.getShoppingCartId() == null || shoppingCartDto.getProducts().isEmpty()) {
            log.info("Для передана пустая корзина.");
            throw new EmptyShoppingCart("передана пустая корзина");
        }

        Map<UUID, Integer> requestedProducts = shoppingCartDto.getProducts();
        List<ProductOfWarehouse> products = repository.findAllById(requestedProducts.keySet());

        if (products.size() < requestedProducts.size()) {
            Set<UUID> foundIds = products.stream()
                    .map(ProductOfWarehouse::getProductId)
                    .collect(Collectors.toSet());
            Set<UUID> missingIds = requestedProducts.keySet().stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new NotFoundProductInWarehouseException("Товары не найдены на складе: " + missingIds);
        }

        Map<UUID, Integer> insufficientProducts = new HashMap<>();
        List<ProductOfWarehouse> verifiedProducts = new ArrayList<>();

        for (ProductOfWarehouse product : products) {
            Integer requestedQty = requestedProducts.get(product.getProductId());

            if (product.getQuantity() >= requestedQty) {
                verifiedProducts.add(product);
            } else {
                insufficientProducts.put(
                        product.getProductId(),
                        (int) (requestedQty - product.getQuantity())
                );
            }
        }

        if (!insufficientProducts.isEmpty()) {
            throw new ProductInShoppingCartNorEnoughInWarehouse(
                    "Товаров недостаточно: " + insufficientProducts
            );
        }

        return BookedProductsDto.builder()

                .deliveryWeight(verifiedProducts.stream()
                        .mapToDouble((product) -> product.getWeight()*product.getQuantity())
                        .sum())

                .deliveryVolume(verifiedProducts.stream()
                        .mapToDouble(product ->
                                product.getDimension().getWidth() *
                                        product.getDimension().getHeight() *
                                        product.getDimension().getDepth() *
                                        product.getQuantity())
                        .sum())

                .fragile(verifiedProducts.stream()
                        .anyMatch(ProductOfWarehouse::getFragile))
                .build();
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.debug("addProductToWarehouse");
        validIdProduct(request.getProductId());
        ProductOfWarehouse product = repository.getReferenceById(request.getProductId());
        product.addQuantity(request.getQuantity());
        repository.save(product);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> products = request.getProducts();
        validateProductsAvailability(products);
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductOfWarehouse product = findProductByIdOrThrow(entry.getKey());
            validateProductQuantity(product, entry.getValue());
            product.reduceQuantity(entry.getValue());
            repository.save(product);
        }
        orderDeliveryRepository.save(OrderDelivery.builder()
                .orderId(request.getOrderId())
                .deliveryId(null)
                .build());
        DeliveryCalculationResult calculation = calculateDeliveryDetails(products);
        return new BookedProductsDto(
                calculation.totalWeight(),
                calculation.hasFragile(),
                calculation.totalVolume()
        );
    }

    private void validIdProduct(UUID idProduct) {
        if (!repository.existsById(idProduct)) {
            throw new NotFoundProductInWarehouseException("Продукт c ID: " + idProduct + " не найден на складе.");
        }
    }

    private void validateProductNotExists(UUID productId) {
        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(productId);
        }
    }

    private ProductOfWarehouse findProductByIdOrThrow(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException());
    }

    private void validateProductsAvailability(Map<UUID, Long> products) {
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();
            ProductOfWarehouse product = findProductByIdOrThrow(productId);
            validateProductQuantity(product, requestedQuantity);
        }
    }

    private void validateProductQuantity(ProductOfWarehouse product, Long requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new ProductInShoppingCartNorEnoughInWarehouse("Указанного продукта недостаточно на складе " +
                    product.getProductId()
            );
        }
    }

    private DeliveryCalculationResult calculateDeliveryDetails(Map<UUID, Long> products) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            ProductOfWarehouse product = findProductByIdOrThrow(productId);
            totalWeight += calculateItemWeight(product, quantity);
            totalVolume += calculateItemVolume(product, quantity);
            if (product.getFragile()) {
                hasFragile = true;
            }
        }
        return new DeliveryCalculationResult(totalWeight, totalVolume, hasFragile);
    }

    private double calculateItemWeight(ProductOfWarehouse product, Long quantity) {
        return product.getWeight() * quantity;
    }

    private double calculateItemVolume(ProductOfWarehouse product, Long quantity) {
        return product.getDimension().getVolume() * quantity;
    }

    private record DeliveryCalculationResult(
            double totalWeight,
            double totalVolume,
            boolean hasFragile
    ) {}
}
