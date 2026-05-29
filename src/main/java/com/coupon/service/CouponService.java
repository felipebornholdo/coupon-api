package com.coupon.service;

import com.coupon.domain.Coupon;
import com.coupon.domain.CouponRepository;
import com.coupon.dto.CouponRequestDTO;
import com.coupon.dto.CouponResponseDTO;
import com.coupon.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponResponseDTO create(CouponRequestDTO request) {
        log.info("Creating coupon with code: {}", request.code());

        var coupon = Coupon.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published());
        var saved = couponRepository.save(coupon);

        log.debug("Coupon persisted with id={} and sanitized code={}", saved.getId(), saved.getCode());

        return CouponResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public CouponResponseDTO findById(UUID id) {
        log.info("Fetching coupon id={}", id);

        var coupon = couponRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Coupon not found for id={}", id);
                    return new ResourceNotFoundException("Coupon not found with id: " + id);
                });

        return CouponResponseDTO.from(coupon);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Attempting soft-delete for coupon id={}", id);

        var coupon = couponRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete — coupon not found for id={}", id);
                    return new ResourceNotFoundException("Coupon not found with id: " + id);
                });

        coupon.delete();
        couponRepository.save(coupon);

        log.info("Coupon id={} soft-deleted successfully", id);
    }
}
