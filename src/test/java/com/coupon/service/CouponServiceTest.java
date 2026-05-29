package com.coupon.service;

import com.coupon.domain.Coupon;
import com.coupon.domain.CouponRepository;
import com.coupon.dto.CouponRequestDTO;
import com.coupon.dto.CouponResponseDTO;
import com.coupon.exception.BusinessException;
import com.coupon.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Tests")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    private Coupon fakeCoupon() {
        return Coupon.create("ABC123", "Desc", new BigDecimal("5.0"), FUTURE, false);
    }

    private CouponRequestDTO buildRequest() {
        return new CouponRequestDTO("ABC123", "Desc", new BigDecimal("5.0"), FUTURE, false);
    }

    @Test
    @DisplayName("create() should save and return DTO with deleted=false")
    void shouldCreateAndReturnDTO() {
        Coupon coupon = fakeCoupon();
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponResponseDTO response = couponService.create(buildRequest());

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("ABC123");
        assertThat(response.deleted()).isFalse();
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("findById() should return DTO when coupon exists")
    void shouldReturnDTOWhenFound() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.of(fakeCoupon()));

        CouponResponseDTO response = couponService.findById(id);

        assertThat(response.code()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("findById() should throw ResourceNotFoundException when not found")
    void shouldThrowNotFoundOnGet() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("delete() should call coupon.delete() and save")
    void shouldSoftDeleteSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = fakeCoupon();
        when(couponRepository.findByIdIncludingDeleted(id)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(coupon)).thenReturn(coupon);

        couponService.delete(id);

        assertThat(coupon.isDeleted()).isTrue();
        verify(couponRepository).save(coupon);
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFoundException when coupon does not exist")
    void shouldThrowNotFoundOnDelete() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findByIdIncludingDeleted(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete() should throw BusinessException when coupon is already deleted")
    void shouldThrowBusinessExceptionWhenAlreadyDeleted() {
        UUID id = UUID.randomUUID();
        Coupon coupon = fakeCoupon();
        coupon.delete();
        when(couponRepository.findByIdIncludingDeleted(id)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> couponService.delete(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Coupon already deleted");
    }
}
