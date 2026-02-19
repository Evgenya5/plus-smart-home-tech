package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.DTO.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.DTO.warehouse.AddressDto;
import ru.yandex.practicum.DTO.warehouse.BookedProductsDto;
import ru.yandex.practicum.DTO.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.interfaces.WarehouseService;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService service;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void addProduct(@Valid @RequestBody NewProductInWarehouseRequest newProduct) {
        log.info("PUT. addProduct");
        service.addNewProductToTheWarehouse(newProduct);
    }

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductQuantityEnough(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("POST. checkProductQuantityEnough");
        return service.checkProductQuantityEnough(shoppingCartDto);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        log.debug("POST. addProductToWarehouse");
        service.addProductToWarehouse(request);
    }

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("Запрос адреса склада");
        return service.getWarehouseAddress();
    }
}
