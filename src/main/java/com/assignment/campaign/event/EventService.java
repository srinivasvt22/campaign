package com.assignment.campaign.event;

import java.time.Clock;
import java.time.Instant;

import com.assignment.campaign.campaign.CampaignRepository;
import com.assignment.campaign.common.NotFoundException;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final CampaignRepository campaignRepository;
    private final EventRepository eventRepository;
    private final Clock clock;

    public EventService(CampaignRepository campaignRepository, EventRepository eventRepository, Clock clock) {
        this.campaignRepository = campaignRepository;
        this.eventRepository = eventRepository;
        this.clock = clock;
    }
    //Ingest events (CLICK or IMPRESSION) with timestamp and cost
    public EventResponse ingest(IngestEventRequest request) {
        if (!campaignRepository.existsById(request.campaignId())) {
            throw new NotFoundException("Campaign not found: " + request.campaignId());
        }

        Event event = new Event(
                request.campaignId(),
                request.type(),
                request.timestamp(),
                request.cost(),
                Instant.now(clock));

        
        return EventResponse.from(eventRepository.insert(event));
    }
}
