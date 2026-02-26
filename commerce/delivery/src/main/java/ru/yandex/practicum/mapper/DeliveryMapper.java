package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.model.AddressEmbeddable;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.DTO.delivery.DeliveryDto;
import ru.yandex.practicum.DTO.warehouse.AddressDto;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DeliveryMapper {

    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "embedToDto")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "embedToDto")
    DeliveryDto toDto(Delivery delivery);

    @Named("embedToDto")
    default AddressDto embedToDto(AddressEmbeddable a) {
        if (a == null) {
            return null;
        }
        return AddressDto.builder()
                .country(a.getCountry())
                .city(a.getCity())
                .street(a.getStreet())
                .house(a.getHouse())
                .flat(a.getFlat())
                .build();
    }

    @Named("dtoToEmbed")
    default AddressEmbeddable dtoToEmbed(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return AddressEmbeddable.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }

    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "dtoToEmbed")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "dtoToEmbed")
    @Mapping(target = "deliveryState", expression = "java(ru.yandex.practicum.enums.delivery.DeliveryState.CREATED)")
    Delivery toEntity(DeliveryDto dto);
}
