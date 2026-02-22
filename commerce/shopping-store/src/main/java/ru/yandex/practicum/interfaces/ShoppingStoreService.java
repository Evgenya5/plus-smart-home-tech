package ru.yandex.practicum.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.DTO.shoppingStore.SetProductQuantity;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;

import java.util.UUID;

public interface ShoppingStoreService {

    Page<ProductDto> findAllByProductCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto dto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean settingTheStatus(SetProductQuantity setProductQuantity);

    ProductDto getProductById(UUID productId);

    Boolean deleteProductFromAssortment(UUID productId);
}
