package com.assignment.campaign.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.assignment.campaign.campaign.CampaignRepository;

class EventServiceTest {

    private final CampaignRepository campaignRepository = org.mockito.Mockito.mock(CampaignRepository.class);
    private final EventRepository eventRepository = org.mockito.Mockito.mock(EventRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-05-13T00:00:00Z"), ZoneOffset.UTC);
    private final EventService eventService = new EventService(campaignRepository, eventRepository, clock);

    //Ingest events (CLICK or IMPRESSION) with timestamp and cost
    @Test
    void ingestsEventForExistingCampaign() {
        when(campaignRepository.existsById("campaign-1")).thenReturn(true);
        when(eventRepository.insert(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId("event-1");
            return event;
        });

        EventResponse response = eventService.ingest(new IngestEventRequest(
                "campaign-1",
                EventType.CLICK,
                Instant.parse("2026-05-13T12:00:00Z"),
                BigDecimal.valueOf(0.35)));

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).insert(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getCampaignId()).isEqualTo("campaign-1");
        assertThat(savedEvent.getType()).isEqualTo(EventType.CLICK);
        assertThat(savedEvent.getTimestamp()).isEqualTo(Instant.parse("2026-05-13T12:00:00Z"));
        assertThat(savedEvent.getCost()).isEqualByComparingTo("0.35");
        assertThat(savedEvent.getReceivedAt()).isEqualTo(Instant.parse("2026-05-13T00:00:00Z"));

        assertThat(response.id()).isEqualTo("event-1");
        assertThat(response.campaignId()).isEqualTo("campaign-1");
        assertThat(response.type()).isEqualTo(EventType.CLICK);
    }

    
}
