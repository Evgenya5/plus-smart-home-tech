package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.DTO.order.CreateNewOrderRequest;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.enums.order.OrderState;
import ru.yandex.practicum.DTO.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.Order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { HashMap.class, OrderState.class }
)
public interface OrderMapper {

    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "shoppingCartId", source = "request.shoppingCart.shoppingCartId")
    @Mapping(target = "products", expression = "java(new HashMap<>(request.getShoppingCart().getProducts()))")
    @Mapping(target = "state", expression = "java(OrderState.NEW)")
    @Mapping(target = "deliveryWeight", source = "deliveryInfo.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "deliveryInfo.deliveryVolume")
    @Mapping(target = "fragile", expression = "java(deliveryInfo.getFragile() != null ? deliveryInfo.getFragile() : false)")
    @Mapping(target = "productPrice", source = "productPrice")
    @Mapping(target = "deliveryPrice", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "totalPrice", source = "productPrice")
    Order toNewOrder(String username,
                     CreateNewOrderRequest request,
                     BookedProductsDto deliveryInfo,
                     BigDecimal productPrice);
}