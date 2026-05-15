package com.assignment.campaign.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyReportResponse(
        LocalDate date,
        long totalClicks,
        long totalImpressions,
        BigDecimal ctr,
        BigDecimal totalSpend) {
}
