package com.ticketsystem.payment.service.mapper;

import com.ticketsystem.payment.domain.Payment;
import com.ticketsystem.payment.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {}
