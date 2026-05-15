package com.assignment.campaign.campaign;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

public record CampaignResponse(
        String id,
        String name,
        BigDecimal budget,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public static CampaignResponse from(Campaign campaign, Clock clock) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getBudget(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.isActiveOn(LocalDate.now(clock)),
                campaign.getCreatedAt(),
                campaign.getUpdatedAt());
    }
}
