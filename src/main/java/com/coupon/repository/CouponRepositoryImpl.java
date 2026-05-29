package com.coupon.repository;

import com.coupon.domain.Coupon;
import com.coupon.domain.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    CouponRepositoryImpl(CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        return jpaRepository.findByExternalIdIncludingDeleted(coupon.getId())
                .map(existing -> {
                    existing.applyFrom(coupon);
                    return jpaRepository.save(existing);
                })
                .orElseGet(() -> jpaRepository.save(CouponEntity.fromDomain(coupon)))
                .toDomain();
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findByExternalId(id)
                .map(CouponEntity::toDomain);
    }

    @Override
    public Optional<Coupon> findByIdIncludingDeleted(UUID id) {
        return jpaRepository.findByExternalIdIncludingDeleted(id)
                .map(CouponEntity::toDomain);
    }
}
