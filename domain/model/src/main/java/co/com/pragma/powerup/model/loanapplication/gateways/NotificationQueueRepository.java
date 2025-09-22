package co.com.pragma.powerup.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface NotificationQueueRepository{
    Mono<String> sendNotification(String message);
}
