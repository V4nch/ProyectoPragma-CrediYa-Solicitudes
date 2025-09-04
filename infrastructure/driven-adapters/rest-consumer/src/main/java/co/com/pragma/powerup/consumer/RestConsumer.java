package co.com.pragma.powerup.consumer;

import co.com.pragma.powerup.consumer.dto.UserResponse;
import co.com.pragma.powerup.consumer.mapper.UserMapper;
import co.com.pragma.powerup.model.user.User;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserRepository {
    private final WebClient client;

    @Override
    public Mono<User> getUserById(String idCard) {
        return client.get()
                .uri(Constants.PATH_USER, idCard)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .timeout(Duration.ofSeconds(5))
                .map(UserMapper::toDomain)
                .onErrorMap(throwable -> {

                    if (throwable instanceof WebClientResponseException) {
                        return new RuntimeException(Constants.LOG_ERROR_USER + throwable.getMessage(), throwable);
                    }
                    return new RuntimeException(Constants.LOG_ERROR_GET_USER, throwable);
                });
    }
}
