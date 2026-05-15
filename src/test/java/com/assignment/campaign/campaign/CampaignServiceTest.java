package com.assignment.campaign.campaign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

class CampaignServiceTest {

    private final CampaignRepository campaignRepository = org.mockito.Mockito.mock(CampaignRepository.class);
    private final MongoTemplate mongoTemplate = org.mockito.Mockito.mock(MongoTemplate.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-05-13T00:00:00Z"), ZoneOffset.UTC);
    private final CampaignService campaignService = new CampaignService(campaignRepository, mongoTemplate, clock);

    @Test
    void createsCampaignWithTrimmedNameAndCurrentTimestamps() {
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign campaign = invocation.getArgument(0);
            campaign.setId("campaign-1");
            return campaign;
        });
        //Create a campaign with name, budget, startDate, endDate
        CampaignResponse response = campaignService.create(new CreateCampaignRequest(
                " NBC Sale ",
                BigDecimal.valueOf(5000),
                LocalDate.parse("2026-05-01"),
                LocalDate.parse("2026-05-31")));

        ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);
        verify(campaignRepository).save(campaignCaptor.capture());

        Campaign savedCampaign = campaignCaptor.getValue();
        assertThat(savedCampaign.getName()).isEqualTo("NBC Sale");
        assertThat(savedCampaign.getBudget()).isEqualByComparingTo("5000");
        assertThat(savedCampaign.getStartDate()).isEqualTo(LocalDate.parse("2026-05-01"));
        assertThat(savedCampaign.getEndDate()).isEqualTo(LocalDate.parse("2026-05-31"));
        assertThat(savedCampaign.getCreatedAt()).isEqualTo(Instant.parse("2026-05-13T00:00:00Z"));
        assertThat(savedCampaign.getUpdatedAt()).isEqualTo(Instant.parse("2026-05-13T00:00:00Z"));

        assertThat(response.id()).isEqualTo("campaign-1");
        assertThat(response.name()).isEqualTo("NBC Sale");
        assertThat(response.active()).isTrue();
    }


    //Retrieve campaigns with filtering (active status, date range).
    @Test
    void findsActiveCampaignsWithinDateWindow() {
        when(mongoTemplate.find(any(Query.class), eq(Campaign.class))).thenReturn(List.of());

        campaignService.find(
                true,
                LocalDate.parse("2026-05-01"),
                LocalDate.parse("2026-05-31"));
    }
}
