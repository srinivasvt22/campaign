package com.assignment.campaign.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import com.assignment.campaign.campaign.CreateCampaignRequest;
import com.assignment.campaign.campaign.CampaignResponse;
import com.assignment.campaign.event.EventType;
import com.assignment.campaign.event.EventResponse;
import com.assignment.campaign.event.IngestEventRequest;
import com.assignment.campaign.report.DailyReportResponse;
import com.assignment.campaign.report.ReportResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CampaignApiIntegrationTest {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @LocalServerPort
    int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void createsCampaignIngestsEventsAndReturnsAggregateReports() {
        String baseUrl = "http://localhost:" + port;//append the baseurl+port
       
        //Get the list of campaigns for the specified date range
        ResponseEntity<CampaignResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/campaigns",
                new CreateCampaignRequest(
                        "Integration Campaign",
                        BigDecimal.valueOf(1000),
                        LocalDate.parse("2026-05-01"),
                        LocalDate.parse("2026-05-31")),
                CampaignResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String campaignId = createResponse.getBody().id();

        ResponseEntity<CampaignResponse[]> campaignsResponse = restTemplate.getForEntity(
                baseUrl + "/api/campaigns?active=true&from=2026-05-01&to=2026-05-31",
                CampaignResponse[].class);
        		
        assertThat(campaignsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(campaignsResponse.getBody()).hasSize(1);

        postEvent(baseUrl, campaignId, EventType.IMPRESSION, "2026-05-13T10:00:00Z", "0.10");
        postEvent(baseUrl, campaignId, EventType.CLICK, "2026-05-13T10:00:01Z", "0.25");
        postEvent(baseUrl, campaignId, EventType.IMPRESSION, "2026-05-14T10:00:00Z", "0.15");

        
        //Get the report for the specified campaignId
        ResponseEntity<ReportResponse> reportResponse = restTemplate.getForEntity(
                baseUrl + "/api/reports/" + campaignId,
                ReportResponse.class);

        assertThat(reportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReportResponse report = reportResponse.getBody();
        assertThat(report.totalClicks()).isEqualTo(1);
        assertThat(report.totalImpressions()).isEqualTo(2);
        assertThat(report.ctr()).isEqualByComparingTo("0.5000");
        assertThat(report.totalSpend()).isEqualByComparingTo("0.50");

        ResponseEntity<DailyReportResponse[]> dailyResponse = restTemplate.getForEntity(
                baseUrl + "/api/reports/" + campaignId + "/daily",
                DailyReportResponse[].class);

        assertThat(dailyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dailyResponse.getBody()).hasSize(2);

        DailyReportResponse firstDay = dailyResponse.getBody()[0];
        assertThat(firstDay.date()).isEqualTo(LocalDate.parse("2026-05-13"));
        assertThat(firstDay.totalClicks()).isEqualTo(1);
        assertThat(firstDay.totalImpressions()).isEqualTo(1);
        assertThat(firstDay.totalSpend()).isEqualByComparingTo("0.35");
    }

    private void postEvent(String baseUrl, String campaignId, EventType type, String timestamp, String cost) {
        ResponseEntity<EventResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/events",
                new IngestEventRequest(campaignId, type, Instant.parse(timestamp), new BigDecimal(cost)),
                EventResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-05-13T00:00:00Z"), ZoneOffset.UTC);
        }
    }
}
