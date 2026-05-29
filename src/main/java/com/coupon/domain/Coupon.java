package com.coupon.domain;

import com.coupon.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Coupon {

    private final UUID id;
    private final String code;
    private final String description;
    private final BigDecimal discountValue;
    private final LocalDateTime expirationDate;
    private final boolean published;
    private boolean deleted;

    private Coupon(UUID id, String code, String description, BigDecimal discountValue,
                   LocalDateTime expirationDate, boolean published, boolean deleted) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.deleted = deleted;
    }

    public static Coupon create(String rawCode, String description, BigDecimal discountValue,
                                LocalDateTime expirationDate, boolean published) {
        var sanitizedCode = sanitizeCode(rawCode);
        validateCode(sanitizedCode);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);
        return new Coupon(UUID.randomUUID(), sanitizedCode, description,
                discountValue, expirationDate, published, false);
    }

    public static Coupon reconstitute(UUID id, String code, String description,
                                      BigDecimal discountValue, LocalDateTime expirationDate,
                                      boolean published, boolean deleted) {
        return new Coupon(id, code, description, discountValue,
                expirationDate, published, deleted);
    }

    public void delete() {
        if (this.deleted) {
            throw new BusinessException("Coupon already deleted");
        }
        this.deleted = true;
    }

    private static String sanitizeCode(String rawCode) {
        if (rawCode == null) return "";
        return rawCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    private static void validateCode(String code) {
        if (code == null || code.length() != 6) {
            throw new BusinessException(
                    "Coupon code must be exactly 6 alphanumeric characters after sanitization"
            );
        }
    }

    private static void validateDiscountValue(BigDecimal value) {
        if (value == null || value.compareTo(new BigDecimal("0.5")) < 0) {
            throw new BusinessException("Discount value must be at least 0.5");
        }
    }

    private static void validateExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null || expirationDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Expiration date cannot be in the past");
        }
    }

    public UUID getId()                      { return id; }
    public String getCode()                  { return code; }
    public String getDescription()           { return description; }
    public BigDecimal getDiscountValue()     { return discountValue; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public boolean isPublished()             { return published; }
    public boolean isDeleted()               { return deleted; }
}