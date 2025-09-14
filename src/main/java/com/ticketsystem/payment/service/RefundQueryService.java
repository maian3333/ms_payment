package com.ticketsystem.payment.service;

import com.ticketsystem.payment.domain.*; // for static metamodels
import com.ticketsystem.payment.domain.Refund;
import com.ticketsystem.payment.repository.RefundRepository;
import com.ticketsystem.payment.service.criteria.RefundCriteria;
import com.ticketsystem.payment.service.dto.RefundDTO;
import com.ticketsystem.payment.service.mapper.RefundMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Refund} entities in the database.
 * The main input is a {@link RefundCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RefundDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RefundQueryService extends QueryService<Refund> {

    private static final Logger LOG = LoggerFactory.getLogger(RefundQueryService.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    public RefundQueryService(RefundRepository refundRepository, RefundMapper refundMapper) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
    }

    /**
     * Return a {@link List} of {@link RefundDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RefundDTO> findByCriteria(RefundCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundMapper.toDto(refundRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RefundCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundRepository.count(specification);
    }

    /**
     * Function to convert {@link RefundCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Refund> createSpecification(RefundCriteria criteria) {
        Specification<Refund> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Refund_.id),
                buildRangeSpecification(criteria.getAmount(), Refund_.amount),
                buildStringSpecification(criteria.getReason(), Refund_.reason),
                buildStringSpecification(criteria.getStatus(), Refund_.status),
                buildRangeSpecification(criteria.getProcessedAt(), Refund_.processedAt),
                buildStringSpecification(criteria.getGatewayRefundId(), Refund_.gatewayRefundId),
                buildSpecification(criteria.getPaymentId(), root -> root.join(Refund_.payment, JoinType.LEFT).get(Payment_.id))
            );
        }
        return specification;
    }
}
