package com.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface CouponJpaRepository extends JpaRepository<CouponEntity, Long> {

    Optional<CouponEntity> findByExternalIdAndDeletedFalse(UUID externalId);

    Optional<CouponEntity> findByExternalId(UUID externalId);
}
