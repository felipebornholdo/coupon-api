package com.coupon.domain;

import com.coupon.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Coupon Domain Tests")
class CouponTest {

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    @Nested
    @DisplayName("Coupon.create()")
    class CreateTests {

        @Test
        @DisplayName("should create coupon with correct fields")
        void shouldCreateValidCoupon() {
            Coupon coupon = Coupon.create("ABC123", "Desc", new BigDecimal("5.0"), FUTURE, false);

            assertThat(coupon.getId()).isNotNull();
            assertThat(coupon.getCode()).isEqualTo("ABC123");
            assertThat(coupon.isPublished()).isFalse();
            assertThat(coupon.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("should create coupon as published when published=true")
        void shouldCreatePublishedCoupon() {
            Coupon coupon = Coupon.create("ABC123", "Desc", new BigDecimal("1.0"), FUTURE, true);
            assertThat(coupon.isPublished()).isTrue();
        }

        @Test
        @DisplayName("should sanitize code by removing special characters")
        void shouldSanitizeSpecialChars() {
            // "AB-C1!23" → remove '-' e '!' → "ABC123"
            Coupon coupon = Coupon.create("AB-C1!23", "Desc", new BigDecimal("1.0"), FUTURE, false);
            assertThat(coupon.getCode()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("should uppercase code during sanitization")
        void shouldUpperCaseCode() {
            Coupon coupon = Coupon.create("abc123", "Desc", new BigDecimal("1.0"), FUTURE, false);
            assertThat(coupon.getCode()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("should throw BusinessException when code has fewer than 6 chars after sanitization")
        void shouldThrowWhenCodeTooShortAfterSanitize() {
            assertThatThrownBy(() ->
                    Coupon.create("AB!@#", "Desc", new BigDecimal("1.0"), FUTURE, false))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("6 alphanumeric characters");
        }

        @Test
        @DisplayName("should throw BusinessException when code has more than 6 chars after sanitization")
        void shouldThrowWhenCodeTooLongAfterSanitize() {
            assertThatThrownBy(() ->
                    Coupon.create("ABCDEFG", "Desc", new BigDecimal("1.0"), FUTURE, false))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("6 alphanumeric characters");
        }

        @Test
        @DisplayName("should throw BusinessException when discount is below 0.5")
        void shouldThrowWhenDiscountBelowMin() {
            assertThatThrownBy(() ->
                    Coupon.create("ABC123", "Desc", new BigDecimal("0.4"), FUTURE, false))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("0.5");
        }

        @Test
        @DisplayName("should accept discount exactly equal to 0.5")
        void shouldAcceptMinimumDiscount() {
            assertThatNoException().isThrownBy(() ->
                    Coupon.create("ABC123", "Desc", new BigDecimal("0.5"), FUTURE, false));
        }

        @Test
        @DisplayName("should throw BusinessException when expiration date is in the past")
        void shouldThrowWhenDateInPast() {
            LocalDateTime past = LocalDateTime.now().minusDays(1);
            assertThatThrownBy(() ->
                    Coupon.create("ABC123", "Desc", new BigDecimal("1.0"), past, false))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("past");
        }

        @Test
        @DisplayName("should throw BusinessException when expiration date is null")
        void shouldThrowWhenDateNull() {
            assertThatThrownBy(() ->
                    Coupon.create("ABC123", "Desc", new BigDecimal("1.0"), null, false))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Coupon.delete()")
    class DeleteTests {

        @Test
        @DisplayName("should mark coupon as deleted")
        void shouldMarkAsDeleted() {
            Coupon coupon = Coupon.create("ABC123", "Desc", new BigDecimal("1.0"), FUTURE, false);
            coupon.delete();
            assertThat(coupon.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("should throw BusinessException when coupon is already deleted")
        void shouldThrowWhenAlreadyDeleted() {
            Coupon coupon = Coupon.create("ABC123", "Desc", new BigDecimal("1.0"), FUTURE, false);
            coupon.delete();

            assertThatThrownBy(coupon::delete)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Coupon already deleted");
        }
    }

    @Nested
    @DisplayName("Coupon.reconstitute()")
    class ReconstituteTests {

        @Test
        @DisplayName("should reconstitute domain object without applying validations")
        void shouldReconstituteWithoutValidations() {
            LocalDateTime pastDate = LocalDateTime.now().minusDays(10);
            assertThatNoException().isThrownBy(() ->
                    Coupon.reconstitute(
                            java.util.UUID.randomUUID(),
                            "ABC123", "Desc",
                            new BigDecimal("5.0"),
                            pastDate, false, false
                    )
            );
        }
    }
}
