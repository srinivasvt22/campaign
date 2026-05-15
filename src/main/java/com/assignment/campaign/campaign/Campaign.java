package com.assignment.campaign.campaign;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "campaigns")
@CompoundIndex(name = "campaign_date_window_idx", def = "{ 'startDate': 1, 'endDate': 1 }")
public class Campaign {

    @Id
    private String id;

    @Indexed
    private String name;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal budget;

    private LocalDate startDate;

    private LocalDate endDate;

    private Instant createdAt;

    private Instant updatedAt;

    // constructor.
    public Campaign() {
    }

    public Campaign(String name, BigDecimal budget, LocalDate startDate, LocalDate endDate, Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isActiveOn(LocalDate date) {
        return !startDate.isAfter(date) && !endDate.isBefore(date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
