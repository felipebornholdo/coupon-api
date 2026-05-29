package com.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface CouponJpaRepository extends JpaRepository<CouponEntity, Long> {

    Optional<CouponEntity> findByExternalId(UUID externalId);

    @Query(value = "SELECT * FROM coupon WHERE external_id = :externalId", nativeQuery = true)
    Optional<CouponEntity> findByExternalIdIncludingDeleted(@Param("externalId") UUID externalId);
}
