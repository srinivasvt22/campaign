package com.assignment.campaign.campaign;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateCampaignRequest(
        @NotBlank String name,
        @NotNull @PositiveOrZero BigDecimal budget,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate) {
}
