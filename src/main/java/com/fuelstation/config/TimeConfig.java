package com.fuelstation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    @Bean
    public Clock businessClock(
            @Value("${app.business-time-zone:America/Sao_Paulo}") String businessTimeZone) {
        return Clock.system(ZoneId.of(businessTimeZone));
    }
}
