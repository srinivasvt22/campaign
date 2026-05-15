package com.assignment.campaign.report;

import java.math.BigDecimal;

public record ReportResponse(
        String campaignId,
        long totalClicks,
        long totalImpressions,
        BigDecimal ctr,
        BigDecimal totalSpend) {
}
