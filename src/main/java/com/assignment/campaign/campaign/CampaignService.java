package com.assignment.campaign.campaign;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.assignment.campaign.common.InvalidRequestException;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public CampaignService(CampaignRepository campaignRepository, MongoTemplate mongoTemplate, Clock clock) {
        this.campaignRepository = campaignRepository;
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }
  //Create a campaign with name, budget, startDate, endDate 	
    public CampaignResponse create(CreateCampaignRequest request) {
        validateCampaignDates(request.startDate(), request.endDate());

        Instant now = Instant.now(clock);
        Campaign campaign = new Campaign(
                request.name().trim(),
                request.budget(),
                request.startDate(),
                request.endDate(),
                now,
                now);

        return CampaignResponse.from(campaignRepository.save(campaign), clock);
    }

  //Retrieve campaigns with filtering (active status, date range).
    public List<CampaignResponse> find(Boolean active, LocalDate from, LocalDate to) {
        validateSearchDates(from, to);

        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "startDate", "name"));
        List<Criteria> criteria = buildCriteria(active, from, to);
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria));
        }

        return mongoTemplate.find(query, Campaign.class)
                .stream()
                .map(campaign -> CampaignResponse.from(campaign, clock))
                .toList();
    }
// Builds criteria based on active, from, and to filters.
    private List<Criteria> buildCriteria(Boolean active, LocalDate from, LocalDate to) {
        List<Criteria> criteria = new ArrayList<>();
        LocalDate today = LocalDate.now(clock);

        if (active != null && active) {
            criteria.add(Criteria.where("startDate").lte(today));
            criteria.add(Criteria.where("endDate").gte(today));
        } else if (active != null) {
            criteria.add(new Criteria().orOperator(
                    Criteria.where("endDate").lt(today),
                    Criteria.where("startDate").gt(today)));
        }

        if (from != null) {
            criteria.add(Criteria.where("endDate").gte(from));
        }

        if (to != null) {
            criteria.add(Criteria.where("startDate").lte(to));
        }

        return criteria;
    }
    // Validation from and to date range.
    private void validateCampaignDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("endDate must be on or after startDate");
        }
    }
    // Validation from and to date range.
    private void validateSearchDates(LocalDate from, LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidRequestException("to must be on or after from");
        }
    }
}
