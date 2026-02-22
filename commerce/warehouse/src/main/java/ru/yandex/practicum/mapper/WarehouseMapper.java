package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.DTO.warehouse.DimensionDto;
import ru.yandex.practicum.DTO.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.ProductOfWarehouse;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    ProductOfWarehouse toEntity(NewProductInWarehouseRequest dto); // главный метод

    Dimension toDimension(DimensionDto dto); // вспомогательный метод

    NewProductInWarehouseRequest toDimensionDto(ProductOfWarehouse product);

    DimensionDto toDimensionDto(Dimension dimension);
}
