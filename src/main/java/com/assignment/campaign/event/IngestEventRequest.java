package com.assignment.campaign.event;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record IngestEventRequest(
        @NotBlank String campaignId,
        @NotNull EventType type,
        @NotNull Instant timestamp,
        @NotNull @PositiveOrZero BigDecimal cost) {
}
