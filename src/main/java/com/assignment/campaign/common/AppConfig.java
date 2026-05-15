package com.assignment.campaign.common;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    Clock clock() {
        // Keep time in UTC.
        return Clock.systemUTC();
    }
}
