package com.assignment.campaign.event;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "events")
@CompoundIndex(name = "event_campaign_timestamp_idx", def = "{ 'campaignId': 1, 'timestamp': 1 }")
@CompoundIndex(name = "event_campaign_type_timestamp_idx", def = "{ 'campaignId': 1, 'type': 1, 'timestamp': 1 }")
public class Event {

    @Id
    private String id;

    @Indexed
    private String campaignId;

    private EventType type;

    private Instant timestamp;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal cost;

    private Instant receivedAt;

    // Constructor.
    public Event() {
    }

    public Event(String campaignId, EventType type, Instant timestamp, BigDecimal cost, Instant receivedAt) {
        this.campaignId = campaignId;
        this.type = type;
        this.timestamp = timestamp;
        this.cost = cost;
        this.receivedAt = receivedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }
}
