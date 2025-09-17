package co.com.pragma.powerup.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface ValidateQueueRepository {
     Mono<String> sendValidation(String message);
}
