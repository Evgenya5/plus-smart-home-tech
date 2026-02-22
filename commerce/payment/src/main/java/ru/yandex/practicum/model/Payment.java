package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_cost", precision = 19, scale = 2)
    private BigDecimal productCost;

    @Column(name = "delivery_total", precision = 19, scale = 2)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", precision = 19, scale = 2)
    private BigDecimal feeTotal;

    @Column(name = "total_payment", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;
}
