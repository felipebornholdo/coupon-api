package com.coupon.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequestDTO(

        @NotBlank(message = "Code is required")
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
        String code,

        @NotBlank(message = "Description is required")
        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
        @DecimalMax(value = "99999.99", message = "Discount value must be at most 99999.99")
        BigDecimal discountValue,

        @NotNull(message = "Expiration date is required")
        @FutureOrPresent(message = "Expiration date must be in the present or future")
        LocalDateTime expirationDate,

        boolean published
) {}

