package com.assignment.campaign;

import com.assignment.campaign.campaign.CampaignRepository;
import com.assignment.campaign.event.EventRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.autoconfigure.exclude="
		+ "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
		+ "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
		+ "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration")
class CampaignApplicationTests {

	@MockitoBean
	CampaignRepository campaignRepository;

	@MockitoBean
	EventRepository eventRepository;

	@MockitoBean
	MongoTemplate mongoTemplate;

	@Autowired
	MockMvc mockMvc;

	
}
