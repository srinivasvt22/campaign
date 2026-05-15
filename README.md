# Campaign Performance Service

Spring Boot service for managing advertising campaigns, ingesting performance events, and reporting campaign metrics from MongoDB aggregation pipelines.

## Stack

- Java 17
- Spring Boot 3
- MongoDB
- Maven
- Docker / Docker Compose

## Setup Steps

Prerequisites:

- Java 17 
- Docker Desktop
- Maven wrapper included in the repository, or a local Maven installation

## Run Locally

#Compile & Build
```mvn clean package

#Install Docker
```docker Desktop

#Build docker image
```docker compose up --build
Hit this url to check application up & running:http://localhost:8080/api/campaigns



The default MongoDB URI is:

```mongodb://root:secret@localhost:27017/campaign?authSource=admin


Override it with `SPRING_DATA_MONGODB_URI`.

## API

##Create a campaign using curl command:

```curl.exe --% -i -X POST http://localhost:8080/api/campaigns -H "Content-Type: application/json" -d "{\"name\":\"NBC  Sale\",\"budget\":5000,\"startDate\":\"2026-05-01\",\"endDate\":\"2026-05-31\"}"


##List campaigns:

```curl "http://localhost:8080/api/campaigns?active=true&from=2026-05-01&to=2026-05-31"
```Hit this url to check record is created or not:http://localhost:8080/api/campaigns

##Ingest an event:

```curl.exe --% -i -X POST http://localhost:8080/api/events -H "Content-Type: application/json" -d “{"campaignId":"6a055f0bf6797029637f0069","type":"CLICK","timestamp":"2026-05-13T12:00:00Z","cost":0.35}”

##Get campaign report:

```
curl http://localhost:8080/api/reports/<campaign-id>
```

###Get daily totals:

```
curl http://localhost:8080/api/reports/<campaign-id>/daily
```


## Data Model

`campaigns`

- `_id`
- `name`
- `budget` stored as MongoDB `Decimal128`
- `startDate`
- `endDate`
- `createdAt`
- `updatedAt`

`events`

- `_id`
- `campaignId`
- `type`: `CLICK` or `IMPRESSION`
- `timestamp`
- `cost` stored as MongoDB `Decimal128`
- `receivedAt`

Events reference campaigns by `campaignId` rather than being embedded.
Campaigns are low-volume parent records while events are high-volume append-only records.

## Indexing

Indexes are created by MongoDB on startup:

- `campaigns`: `{ startDate: 1, endDate: 1 }` for active/date-range filtering.
- `campaigns`: `{ name: 1 }` for name lookup or operational search.
- `events`: `{ campaignId: 1 }` for campaign validation or report access patterns.
- `events`: `{ campaignId: 1, timestamp: 1 }` for campaign time-series reporting.
- `events`: `{ campaignId: 1, type: 1, timestamp: 1 }` for grouped click or impression reporting.

## Architecture

The application follows a controller-service-repository workflow:

- `campaign`: owns campaign creation, search or filtering, campaign DTOs, and the campaign MongoDB document.
- `event`: owns event ingestion, campaign existence validation, event DTOs, and the event MongoDB document.
- `report`: owns read-only reporting APIs backed by MongoDB aggregation pipelines.
- `common`: global configuration, domain exceptions, and customized API error responses.



- Controllers expose HTTP endpoints and bind or validate request DTOs.
- Services hold business rules: date validation, campaign lookup, event, and active campaign filtering.
- Repositories handle simple MongoDB JPA.
- `MongoTemplate` is used where dynamic filters or aggregation pipelines are needed.

- Validation is handled with Jakarta Bean Validation and business rules in services.
- Schema design uses referencing rather than embedding. 
- Campaigns are stored in the `campaigns` collection, and events are stored in the `events` 
- collection with a `campaignId` reference. 

## Testing

Run:

```
./mvnw test
```

The test suite includes unit tests for service validation and successful service behavior. 
It also includes an API integration test using Testcontainers MongoDB. 


## Assumptions

- Spring Boot 3 requires Java 17 for satisfying the Java 11+ requirement.
- Campaign active status is computed as `startDate <= today <= endDate` in UTC.
- Daily reports are grouped by event timestamp date in UTC.
- Event ingestion validates that the campaign exists, then inserts an immutable event record.
- Events are append-only; correction or deletion workflows are outside the current scope.
- Campaign events can be high-volume, so they are referenced by `campaignId` instead of embedded in campaign documents.
- Monetary values are stored as `Decimal128` to avoid floating point precision issues.
- CTR is `0.0000` when impressions are zero.
