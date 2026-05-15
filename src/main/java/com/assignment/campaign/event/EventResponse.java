package com.assignment.campaign.event;

import java.math.BigDecimal;
import java.time.Instant;

public record EventResponse(
        String id,
        String campaignId,
        EventType type,
        Instant timestamp,
        BigDecimal cost,
        Instant receivedAt) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getCampaignId(),
                event.getType(),
                event.getTimestamp(),
                event.getCost(),
                event.getReceivedAt());
    }
}
