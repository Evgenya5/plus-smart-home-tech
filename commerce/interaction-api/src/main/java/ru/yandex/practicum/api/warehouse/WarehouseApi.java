package ru.yandex.practicum.api.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.DTO.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseApi {

    @PutMapping
    void addProduct(@RequestBody NewProductInWarehouseRequest newProduct);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnough(@RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/return")
    void acceptReturn(@RequestBody Map<UUID, Long> products);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);
}
