package com.coupon.controller;

import com.coupon.dto.CouponRequestDTO;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("CouponController Integration Tests")
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    private CouponRequestDTO buildRequest() {
        return new CouponRequestDTO("ABC123", "Desconto integração",
                new BigDecimal("5.00"), FUTURE, false);
    }

    private String createAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asString();
    }


    @Test
    @DisplayName("POST /coupon → 201 with correct response body")
    void shouldReturn201OnCreate() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    @DisplayName("POST /coupon → 201 and sanitized code returned")
    void shouldReturnSanitizedCode() throws Exception {
        CouponRequestDTO req = new CouponRequestDTO(
                "AB-C1!23", "Desc", new BigDecimal("1.0"), FUTURE, false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    @DisplayName("POST /coupon → 400 for past expiration date")
    void shouldReturn400ForPastDate() throws Exception {
        CouponRequestDTO req = new CouponRequestDTO(
                "ABC123", "Desc", new BigDecimal("1.0"),
                LocalDateTime.now().minusDays(1), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /coupon → 400 for discount below minimum")
    void shouldReturn400ForLowDiscount() throws Exception {
        CouponRequestDTO req = new CouponRequestDTO(
                "ABC123", "Desc", new BigDecimal("0.4"), FUTURE, false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /coupon → 400 for missing required fields")
    void shouldReturn400WhenMissingFields() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /coupon/{id} → 200 for existing coupon")
    void shouldReturn200OnGet() throws Exception {
        String id = createAndGetId();

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    @DisplayName("GET /coupon/{id} → 404 for unknown ID")
    void shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get("/coupon/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /coupon/{id} → 404 after soft delete")
    void shouldReturn404AfterDelete() throws Exception {
        String id = createAndGetId();
        mockMvc.perform(delete("/coupon/" + id));

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /coupon/{id} → 204 on first delete")
    void shouldReturn204OnDelete() throws Exception {
        String id = createAndGetId();

        mockMvc.perform(delete("/coupon/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /coupon/{id} → 400 when deleting an already deleted coupon")
    void shouldReturn400WhenDeletingTwice() throws Exception {
        String id = createAndGetId();
        mockMvc.perform(delete("/coupon/" + id));

        mockMvc.perform(delete("/coupon/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Coupon already deleted"));
    }

    @Test
    @DisplayName("DELETE /coupon/{id} → 404 for unknown ID")
    void shouldReturn404OnDeleteUnknownId() throws Exception {
        mockMvc.perform(delete("/coupon/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
