package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "order_delivery", schema = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDelivery {

    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;
}
