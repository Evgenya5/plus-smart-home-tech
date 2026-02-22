package ru.yandex.practicum.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.OrderDelivery;

import java.util.UUID;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, UUID> {
}
