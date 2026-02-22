package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.DTO.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.enums.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { PaymentStatus.class }
)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "status", expression = "java(PaymentStatus.PENDING)")
    Payment toEntity(UUID orderId, BigDecimal productCost, BigDecimal deliveryTotal,
                     BigDecimal feeTotal, BigDecimal totalPayment);
}
