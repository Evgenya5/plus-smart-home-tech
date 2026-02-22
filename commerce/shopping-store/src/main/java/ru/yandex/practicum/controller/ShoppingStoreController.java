package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.DTO.shoppingStore.SetProductQuantity;
import ru.yandex.practicum.enums.shoppingStore.ProductCategory;
import ru.yandex.practicum.enums.shoppingStore.QuantityState;
import ru.yandex.practicum.interfaces.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {
    private final ShoppingStoreService service;

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("PUT. addProduct");
        return service.createProduct(productDto);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable
                                     @NotNull(message = "ID продукта != null.") UUID productId) {
        log.info("GET. getProductById ID - {}", productId);
        return service.getProductById(productId);
    }

    @GetMapping
    public Page<ProductDto> getProductsByCategory(
            @NotNull(message = "Категория продукта != null.")
            @RequestParam ProductCategory category,
            @PageableDefault(page = 0, size = 20, sort = "productName", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("GET. getProductsByCategory {}", category);
        return service.findAllByProductCategory(category, pageable);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("POST. updateProduct ID - {}", productDto.getProductId());
        return service.updateProduct(productDto);
    }

    @PostMapping("/quantityState")
    public boolean setQuantityState(UUID productId, QuantityState quantityState) {
        log.info("POST. setQuantityState ID - {} quantity = {}",
                productId, quantityState);
        return service.settingTheStatus(SetProductQuantity.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody
                                 @NotNull(message = "ID продукта != null.") UUID productId) {
        log.info("POST. removeProduct ID - {}", productId);
        return service.deleteProductFromAssortment(productId);
    }
}
