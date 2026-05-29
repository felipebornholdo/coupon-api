package com.coupon.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequestDTO(

        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
        BigDecimal discountValue,

        @NotNull(message = "Expiration date is required")
        LocalDateTime expirationDate,

        boolean published
) {}

