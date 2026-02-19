package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.DTO.shoppingCart.ShoppingCartDto;
import ru.yandex.practicum.DTO.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.DTO.warehouse.AddressDto;
import ru.yandex.practicum.DTO.warehouse.BookedProductsDto;
import ru.yandex.practicum.DTO.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProductToTheWarehouse(NewProductInWarehouseRequest newProduct);

    BookedProductsDto checkProductQuantityEnough(ShoppingCartDto shoppingCartDto);

    AddressDto getWarehouseAddress();

    void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest);
}
