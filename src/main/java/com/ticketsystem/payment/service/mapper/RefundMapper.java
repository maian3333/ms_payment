package com.ticketsystem.payment.service.mapper;

import com.ticketsystem.payment.domain.Payment;
import com.ticketsystem.payment.domain.Refund;
import com.ticketsystem.payment.service.dto.PaymentDTO;
import com.ticketsystem.payment.service.dto.RefundDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refund} and its DTO {@link RefundDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundMapper extends EntityMapper<RefundDTO, Refund> {
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    RefundDTO toDto(Refund s);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);
}
