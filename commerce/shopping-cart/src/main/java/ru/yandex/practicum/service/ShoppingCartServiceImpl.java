package ru.yandex.practicum.service;

import feign.FeignException;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.DTO.shoppingCart.ChangeProductQuantityRequest;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.api.warehouse.WarehouseApi;
import ru.yandex.practicum.enums.shoppingCart.CartState;
import ru.yandex.practicum.exception.shoppingCart.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.shoppingCart.NotAuthorizedException;
import ru.yandex.practicum.interfaces.ShoppingCartRepository;
import ru.yandex.practicum.interfaces.ShoppingCartService;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseApi warehouseApi;

    private static final String CART_IS_DEACTIVATE = "Корзина с ID {} находится в статусе 'DEACTIVATE'" +
            " в результате чего нельзя добавлять новые предметы.";

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCartByUser(String username) {
        return mapper.toDto(findByUsernameOrCreateNew(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductsAtShoppingCart(String username, Map<UUID, Integer> products) {
        ShoppingCart shoppingCart;
        // Если у корзины, куда добавляются новые товары, нет id - создаем новую корзину.
        shoppingCart = findByUsernameOrCreateNew(username);
        // Если найденная корзина имеет статус отличный от DEACTIVATE - добавляем в нее новые товары.
        if (!CartState.DEACTIVATE.equals(shoppingCart.getCartState())) {
            Map<UUID, Integer> currentProducts = shoppingCart.getProducts();
            products.forEach((productId, quantity) -> {
                Integer currentQuantity = currentProducts.getOrDefault(productId, 0);
                currentProducts.put(productId, currentQuantity + quantity);
            });
            shoppingCart.setProducts(currentProducts);
            //ShoppingCartDto shoppingCartDto = mapper.toDto(shoppingCart);
            //mapper.addOnlyNewProducts(shoppingCartDto, shoppingCart);
            String idsList = shoppingCart.getProducts().keySet().stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(", "));
            //log.info(shoppingCartDto.toString());
            checkProductQuantityEnoughForShoppingCart(mapper.toDto(shoppingCart));
            repository.save(shoppingCart);
            log.info("Товар(-ы) c ID {} успешно добавлен.", idsList);
            // Если статус DEACTIVATE - выводим log и возвращаем корзину без изменений.
        } else {
            log.info(CART_IS_DEACTIVATE, shoppingCart.getShoppingCartId());
        }
        return mapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void deactivateShoppingCart(String username) {
        ShoppingCart shoppingCart = findByUsernameOrElseThrow(username);
        if (shoppingCart.getCartState().equals(CartState.DEACTIVATE)) {
            log.info("Корзина пользователя {} уже деактивирована. ID корзины: {}.",
                    username,
                    shoppingCart.getShoppingCartId());
            return;
        }
        shoppingCart.setCartState(CartState.DEACTIVATE);
        log.info("Корзина пользователя {} деактивирована. ID корзины: {}.",
                username,
                shoppingCart.getShoppingCartId());
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantityInTheBasket(String username, ChangeProductQuantityRequest changeQuantity) {
        ShoppingCart shoppingCart = findByUsernameOrCreateNew(username);

        if (!shoppingCart.getProducts().containsKey(changeQuantity.getProductId())) {
            throw new NoProductsInShoppingCartException("Корзина не содержит изменяемые товары.");
        }
        if (!CartState.DEACTIVATE.equals(shoppingCart.getCartState())) {
            if (shoppingCart.getProducts().get(changeQuantity.getProductId()) < changeQuantity.getNewQuantity()) {
                checkProductQuantityEnoughForShoppingCart(ShoppingCartDto.builder()
                        .shoppingCartId(shoppingCart.getShoppingCartId())
                        .products(Map.of(changeQuantity.getProductId(), changeQuantity.getNewQuantity()))
                        .build()
                );
            }
            shoppingCart.getProducts().put(
                    changeQuantity.getProductId(),
                    changeQuantity.getNewQuantity());
            log.info("Количество товара успешно обновлено.");
        } else {
            log.info(CART_IS_DEACTIVATE, shoppingCart.getShoppingCartId());
        }
        return mapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto deleteProductsFromShoppingCart(String username, Set<UUID> productIds) {
        ShoppingCart shoppingCart = findByUsernameOrElseThrow(username);
        if (!CartState.DEACTIVATE.equals(shoppingCart.getCartState())) {
            Map<UUID, Integer> products = shoppingCart.getProducts();

            productIds.stream()
                    .filter(products::containsKey)
                    .forEach(products::remove);

            log.info("Товары с ID: {} успешно удалены из корзины пользователя {}", productIds, username);
        } else {
            log.info(CART_IS_DEACTIVATE, shoppingCart.getShoppingCartId());
        }
        return mapper.toDto(shoppingCart);
    }

    private ShoppingCart findByUsernameOrElseThrow(String username) {
        log.info("Попытка получить корзину пользователя.");
        return repository.findByUsername(username).orElseThrow(() ->
                new NotAuthorizedException("Корзина пользователя " + username + " не найдена."));
    }

    private ShoppingCart findByUsernameOrCreateNew(String username) {
        log.info("Попытка получить корзину пользователя.");
        return repository.findByUsername(username).orElseGet(() -> createNewCart(username, new HashMap<>()));
    }

    private ShoppingCart createNewCart(String username, Map<UUID, Integer> newProducts) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .username(username)
                .products(newProducts)
                .build();

        repository.save(shoppingCart);
        log.info("Создана новая корзина с ID: {} для пользователя с именем: {}",
                shoppingCart.getShoppingCartId(), username);
        return shoppingCart;
    }

    private void checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        try {
            warehouseApi.checkProductQuantityEnough(shoppingCartDto);
        } catch (FeignException e) {
            log.error("Ошибка при проверке наличия товаров на складе: {}", e.getMessage());

            if (e.status() == 400) {
                throw new IllegalArgumentException("Товары недоступны в запрашиваемом количестве");
            }
        }
    }
}