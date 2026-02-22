package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.AddressEmbeddable;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.api.order.OrderApi;
import ru.yandex.practicum.api.warehouse.WarehouseApi;
import ru.yandex.practicum.DTO.delivery.DeliveryDto;
import ru.yandex.practicum.enums.delivery.DeliveryState;
import ru.yandex.practicum.DTO.order.OrderDto;
import ru.yandex.practicum.DTO.warehouse.ShippedToDeliveryRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final double BASE_COST = 5.0;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderApi orderApi;
    private final WarehouseApi warehouseApi;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto request) {
        Delivery delivery = deliveryMapper.toEntity(request);
        delivery.setDeliveryVolume(request.getDeliveryVolume());
        delivery.setDeliveryWeight(request.getDeliveryWeight());
        delivery.setFragile(Boolean.TRUE.equals(request.getFragile()));
        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public double deliveryCost(OrderDto orderDto) {
        Delivery delivery = getDeliveryByOrderOrThrow(orderDto.getOrderId());
        return calculateCost(
                delivery.getFromAddress(),
                delivery.getToAddress(),
                orderDto.getDeliveryWeight() != null ? orderDto.getDeliveryWeight() : 0.0,
                orderDto.getDeliveryVolume() != null ? orderDto.getDeliveryVolume() : 0.0,
                Boolean.TRUE.equals(orderDto.getFragile())
        );
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = getDeliveryByOrderOrThrow(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        orderApi.assembly(orderId);
        warehouseApi.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        updateDeliveryState(orderId, DeliveryState.DELIVERED);
        orderApi.delivery(orderId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        updateDeliveryState(orderId, DeliveryState.FAILED);
        orderApi.deliveryFailed(orderId);
    }

    private Delivery getDeliveryByOrderOrThrow(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(orderId));
    }

    private void updateDeliveryState(UUID orderId, DeliveryState newState) {
        Delivery delivery = getDeliveryByOrderOrThrow(orderId);
        delivery.setDeliveryState(newState);
        deliveryRepository.save(delivery);
    }

    private double calculateCost(AddressEmbeddable fromAddress, AddressEmbeddable toAddress,
                         double weight, double volume, boolean fragile) {
        double sum = BASE_COST;
        double factor = warehouseAddressFactor(fromAddress);
        sum = sum + (BASE_COST * factor);
        if (fragile) {
            sum = sum + (sum * 0.2);
        }
        sum = sum + (weight * 0.3);
        sum = sum + (volume * 0.2);
        if (!isSameStreet(fromAddress, toAddress)) {
            sum = sum + (sum * 0.2);
        }
        return sum;
    }

    private double warehouseAddressFactor(AddressEmbeddable address) {
        String s = toAddressString(address);
        if (s.contains("ADDRESS_2")) return 2.0;
        if (s.contains("ADDRESS_1")) return 1.0;
        return 1.0;
    }

    private boolean isSameStreet(AddressEmbeddable from, AddressEmbeddable to) {
        if (from == null || to == null) return false;
        String fromStreet = from.getStreet() != null ? from.getStreet().trim() : "";
        String toStreet = to.getStreet() != null ? to.getStreet().trim() : "";
        return fromStreet.equalsIgnoreCase(toStreet);
    }

    private String toAddressString(AddressEmbeddable a) {
        if (a == null) return "";
        return String.join(" ", nullToEmpty(a.getCountry()), nullToEmpty(a.getCity()),
                nullToEmpty(a.getStreet()), nullToEmpty(a.getHouse()), nullToEmpty(a.getFlat()));
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}
