package com.assignment.campaign.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.assignment.campaign.campaign.CampaignRepository;
import com.assignment.campaign.common.NotFoundException;
import com.assignment.campaign.event.EventType;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private static final String EVENT_COLLECTION = "events";
    private static final int CTR_SCALE = 4;

    private final CampaignRepository campaignRepository;
    private final MongoTemplate mongoTemplate;

    public ReportService(CampaignRepository campaignRepository, MongoTemplate mongoTemplate) {
        this.campaignRepository = campaignRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public ReportResponse getCampaignReport(String campaignId) {
        ensureCampaignExists(campaignId);

        // Return totalClicks, totalImpressions, CTR, and totalSpend .
        Document group = new Document("_id", "$campaignId")
                .append("totalClicks", countByType(EventType.CLICK))
                .append("totalImpressions", countByType(EventType.IMPRESSION))
                .append("totalSpend", new Document("$sum", "$cost"));
        //aggregation pipeline
        Document result = events().aggregate(List.of(
                new Document("$match", new Document("campaignId", campaignId)),
                new Document("$group", group))).first();

        if (result == null) {
            return new ReportResponse(campaignId, 0, 0, BigDecimal.ZERO.setScale(CTR_SCALE), BigDecimal.ZERO);
        }

        long clicks = longValue(result.get("totalClicks"));
        long impressions = longValue(result.get("totalImpressions"));
        BigDecimal spend = decimalValue(result.get("totalSpend"));

        return new ReportResponse(campaignId, clicks, impressions, ctr(clicks, impressions), spend);
    }
     //Return daily aggregated metrics
    public List<DailyReportResponse> getDailyReport(String campaignId) {
        ensureCampaignExists(campaignId);

        Document dateExpression = new Document("$dateToString", new Document("format", "%Y-%m-%d")
                .append("date", "$timestamp")
                .append("timezone", "UTC"));

        Document group = new Document("_id", dateExpression)
                .append("totalClicks", countByType(EventType.CLICK))
                .append("totalImpressions", countByType(EventType.IMPRESSION))
                .append("totalSpend", new Document("$sum", "$cost"));

        return events().aggregate(List.of(
                        new Document("$match", new Document("campaignId", campaignId)),
                        new Document("$group", group),
                        new Document("$sort", new Document("_id", 1))))
                .map(result -> {
                    long clicks = longValue(result.get("totalClicks"));
                    long impressions = longValue(result.get("totalImpressions"));
                    return new DailyReportResponse(
                            LocalDate.parse(result.getString("_id")),
                            clicks,
                            impressions,
                            ctr(clicks, impressions),
                            decimalValue(result.get("totalSpend")));
                })
                .into(new ArrayList<>());
    }

    private void ensureCampaignExists(String campaignId) {
        if (!campaignRepository.existsById(campaignId)) {
            throw new NotFoundException("Campaign not found: " + campaignId);
        }
    }

    private MongoCollection<Document> events() {
        return mongoTemplate.getCollection(EVENT_COLLECTION);
    }

    private Document countByType(EventType type) {
        return new Document("$sum", new Document("$cond", Arrays.asList(
                new Document("$eq", Arrays.asList("$type", type.name())),
                1,
                0)));
    }

    private BigDecimal ctr(long clicks, long impressions) {
        if (impressions == 0) {
            return BigDecimal.ZERO.setScale(CTR_SCALE);
        }
        return BigDecimal.valueOf(clicks).divide(BigDecimal.valueOf(impressions), CTR_SCALE, RoundingMode.HALF_UP);
    }

    private long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof Decimal128 decimal128) {
            return decimal128.bigDecimalValue();
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
