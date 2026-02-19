package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.DTO.shoppingStore.SetProductQuantity;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;
import ru.yandex.practicum.enums.shoppingStore.ProductState;
import ru.yandex.practicum.exception.shoppingStore.ProductNotFoundException;
import ru.yandex.practicum.interfaces.RepositoryShoppingStore;
import ru.yandex.practicum.interfaces.ShoppingStoreService;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final RepositoryShoppingStore repository;
    private final ShoppingStoreMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllByProductCategory(ProductCategory category, Pageable pageable) {

        return repository.findAllByProductCategory(category, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto dto) {

        Product product = mapper.toEntity(dto);
        Product savedProduct = repository.save(product);
        return mapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {

        Product product = repository.findById(productDto.getProductId()).orElseThrow(NotFoundException::new);
        mapper.updateProduct(productDto, product);
        log.info("Update product with ID: {}", productDto.getProductId());
        return mapper.toDto(product);
    }

    @Override
    @Transactional
    public Boolean settingTheStatus(SetProductQuantity setProductQuantity) {

        Product product = repository.findById(setProductQuantity.getProductId()).orElseThrow(
                () -> new ProductNotFoundException("Product not found"));

        product.setQuantityState(setProductQuantity.getQuantityState());
        repository.save(product);

        log.info("change product ID {} set status {}", setProductQuantity.getProductId(),
                setProductQuantity.getQuantityState());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {

        Product product = repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product " + productId + " not found"));

        return mapper.toDto(product);
    }

    @Override
    @Transactional
    public Boolean deleteProductFromAssortment(UUID productId) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product " + productId + " not found"));

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);

        log.debug("Product with ID {} delete", productId);
        return true;
    }
}
