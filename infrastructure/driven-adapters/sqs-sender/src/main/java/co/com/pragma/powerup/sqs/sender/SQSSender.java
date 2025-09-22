package co.com.pragma.powerup.sqs.sender;

import co.com.pragma.powerup.model.loanapplication.gateways.NotificationQueueRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.ValidateQueueRepository;
import co.com.pragma.powerup.model.loanapplication.messageSQS.ValidationMessage;
import co.com.pragma.powerup.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationQueueRepository, ValidateQueueRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> sendNotification(String message) {
        return send(properties.queueUrlNotify(), message);
    }

    public Mono<String> sendValidation(ValidationMessage message) {
        try {
        String body = objectMapper.writeValueAsString(message);
        return send(properties.queueUrlValidate(), body);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<String> send(String queueUrl, String message) {

        return Mono.fromCallable(() -> buildRequest(queueUrl, message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String queueUrl, String message) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }

}
