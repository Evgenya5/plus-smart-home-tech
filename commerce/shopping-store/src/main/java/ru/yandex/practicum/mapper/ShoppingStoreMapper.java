package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.DTO.shoppingStore.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ShoppingStoreMapper {

    Product toEntity(ProductDto dto);

    ProductDto toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", ignore = true)
    void updateProduct(ProductDto dto, @MappingTarget Product product);
}
