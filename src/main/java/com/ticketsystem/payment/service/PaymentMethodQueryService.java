package com.ticketsystem.payment.service;

import com.ticketsystem.payment.domain.*; // for static metamodels
import com.ticketsystem.payment.domain.PaymentMethod;
import com.ticketsystem.payment.repository.PaymentMethodRepository;
import com.ticketsystem.payment.service.criteria.PaymentMethodCriteria;
import com.ticketsystem.payment.service.dto.PaymentMethodDTO;
import com.ticketsystem.payment.service.mapper.PaymentMethodMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PaymentMethod} entities in the database.
 * The main input is a {@link PaymentMethodCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PaymentMethodDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PaymentMethodQueryService extends QueryService<PaymentMethod> {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentMethodQueryService.class);

    private final PaymentMethodRepository paymentMethodRepository;

    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodQueryService(PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    /**
     * Return a {@link List} of {@link PaymentMethodDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> findByCriteria(PaymentMethodCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PaymentMethod> specification = createSpecification(criteria);
        return paymentMethodMapper.toDto(paymentMethodRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PaymentMethodCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PaymentMethod> specification = createSpecification(criteria);
        return paymentMethodRepository.count(specification);
    }

    /**
     * Function to convert {@link PaymentMethodCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PaymentMethod> createSpecification(PaymentMethodCriteria criteria) {
        Specification<PaymentMethod> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PaymentMethod_.id),
                buildSpecification(criteria.getUserId(), PaymentMethod_.userId),
                buildStringSpecification(criteria.getType(), PaymentMethod_.type),
                buildStringSpecification(criteria.getProvider(), PaymentMethod_.provider),
                buildStringSpecification(criteria.getMaskedDetails(), PaymentMethod_.maskedDetails),
                buildSpecification(criteria.getIsDefault(), PaymentMethod_.isDefault),
                buildRangeSpecification(criteria.getExpiresAt(), PaymentMethod_.expiresAt)
            );
        }
        return specification;
    }
}
