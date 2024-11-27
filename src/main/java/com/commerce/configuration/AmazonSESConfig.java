package com.commerce.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AmazonSESConfig {

    @Value("${AWS_REGION}")
    private String awsRegion;

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

}
