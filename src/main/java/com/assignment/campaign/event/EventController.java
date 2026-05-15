package com.assignment.campaign.event;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
   //Ingest events with campaignId
    @PostMapping
    ResponseEntity<EventResponse> ingest(@Valid @RequestBody IngestEventRequest request) {
        EventResponse event = eventService.ingest(request);
        return ResponseEntity
                .created(URI.create("/api/events/" + event.id()))
                .body(event);
    }
}
