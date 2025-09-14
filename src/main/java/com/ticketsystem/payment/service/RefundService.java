package com.ticketsystem.payment.service;

import com.ticketsystem.payment.service.dto.RefundDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ticketsystem.payment.domain.Refund}.
 */
public interface RefundService {
    /**
     * Save a refund.
     *
     * @param refundDTO the entity to save.
     * @return the persisted entity.
     */
    RefundDTO save(RefundDTO refundDTO);

    /**
     * Updates a refund.
     *
     * @param refundDTO the entity to update.
     * @return the persisted entity.
     */
    RefundDTO update(RefundDTO refundDTO);

    /**
     * Partially updates a refund.
     *
     * @param refundDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RefundDTO> partialUpdate(RefundDTO refundDTO);

    /**
     * Get the "id" refund.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RefundDTO> findOne(Long id);

    /**
     * Delete the "id" refund.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
