package ru.yandex.practicum.exception.warehouse;

import java.util.UUID;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super(
                "Данный продукт уже существует на складе" + productId
        );
    }
}
