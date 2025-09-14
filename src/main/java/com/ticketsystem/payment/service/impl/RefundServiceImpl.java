package com.ticketsystem.payment.service.impl;

import com.ticketsystem.payment.domain.Refund;
import com.ticketsystem.payment.repository.RefundRepository;
import com.ticketsystem.payment.service.RefundService;
import com.ticketsystem.payment.service.dto.RefundDTO;
import com.ticketsystem.payment.service.mapper.RefundMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ticketsystem.payment.domain.Refund}.
 */
@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final Logger LOG = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    public RefundServiceImpl(RefundRepository refundRepository, RefundMapper refundMapper) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
    }

    @Override
    public RefundDTO save(RefundDTO refundDTO) {
        LOG.debug("Request to save Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public RefundDTO update(RefundDTO refundDTO) {
        LOG.debug("Request to update Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public Optional<RefundDTO> partialUpdate(RefundDTO refundDTO) {
        LOG.debug("Request to partially update Refund : {}", refundDTO);

        return refundRepository
            .findById(refundDTO.getId())
            .map(existingRefund -> {
                refundMapper.partialUpdate(existingRefund, refundDTO);

                return existingRefund;
            })
            .map(refundRepository::save)
            .map(refundMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefundDTO> findOne(Long id) {
        LOG.debug("Request to get Refund : {}", id);
        return refundRepository.findById(id).map(refundMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Refund : {}", id);
        refundRepository.deleteById(id);
    }
}
