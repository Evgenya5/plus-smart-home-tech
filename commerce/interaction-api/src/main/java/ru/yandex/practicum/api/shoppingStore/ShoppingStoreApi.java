package ru.yandex.practicum.api.shoppingStore;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.DTO.shoppingStore.SetProductQuantity;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @PutMapping
    ProductDto addProduct(@RequestBody ProductDto productDto);

    @GetMapping("/{productId}")
    ProductDto getProductById(@RequestParam UUID productId);

    @GetMapping
    Page<ProductDto> getProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/quantityState")
    boolean updateQuantityState(@RequestBody SetProductQuantity setProductQuantity);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);
}
