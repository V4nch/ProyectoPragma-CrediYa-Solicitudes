package co.com.pragma.powerup.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SQSSenderProperties(
     String region,
     String queueUrlNotify,
     String queueUrlValidate,
     String queueUrlReport,
     String endpoint){
}
