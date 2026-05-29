package com.coupon.repository;

import com.coupon.domain.Coupon;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon", indexes = @Index(name = "idx_coupon_external_id", columnList = "externalId"))
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID externalId;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean deleted;

    protected CouponEntity() {}

    private CouponEntity(UUID externalId, String code, String description, BigDecimal discountValue,
                         LocalDateTime expirationDate, boolean published, boolean deleted) {
        this.externalId = externalId;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.deleted = deleted;
    }

    public static CouponEntity fromDomain(Coupon coupon) {
        return new CouponEntity(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.isPublished(),
                coupon.isDeleted()
        );
    }

    public Coupon toDomain() {
        return Coupon.reconstitute(
                this.externalId,
                this.code,
                this.description,
                this.discountValue,
                this.expirationDate,
                this.published,
                this.deleted
        );
    }

    public void applyFrom(Coupon coupon) {
        this.deleted = coupon.isDeleted();
        this.published = coupon.isPublished();
    }

    public Long getId()          { return id; }
    public UUID getExternalId()  { return externalId; }
}
