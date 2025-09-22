package co.com.pragma.powerup.model.loanapplication.gateways;

import co.com.pragma.powerup.model.loanapplication.messageSQS.ValidationMessage;
import reactor.core.publisher.Mono;

public interface ValidateQueueRepository {
     Mono<String> sendValidation(ValidationMessage message);
}
