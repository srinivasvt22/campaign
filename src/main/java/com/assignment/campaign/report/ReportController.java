package com.assignment.campaign.report;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    //Return totalClicks, totalImpressions, CTR, and totalSpend 
    @GetMapping("/{campaignId}")
    ReportResponse campaignReport(@PathVariable String campaignId) {
        return reportService.getCampaignReport(campaignId);
    }
    //Return daily aggregated metrics
    @GetMapping("/{campaignId}/daily")
    List<DailyReportResponse> dailyReport(@PathVariable String campaignId) {
        return reportService.getDailyReport(campaignId);
    }
}
