package co.com.pragma.powerup.consumer;

import co.com.pragma.powerup.consumer.dto.UserResponse;
import co.com.pragma.powerup.consumer.mapper.UserMapper;
import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.debtcapacity.response.CapacityResponse;
import co.com.pragma.powerup.model.loanapplication.gateways.CapacityRestRepository;
import co.com.pragma.powerup.model.user.User;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserRepository, CapacityRestRepository {
    private final WebClient client;

    @Override
    public Mono<User> getUserByIdCard(String idCard) {
        return Mono.deferContextual(ctx -> {
            String token = ctx.get("authToken");
            return client.get()
                    .uri(Constants.PATH_USER, idCard)
                    .header(HttpHeaders.AUTHORIZATION, token)
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
        });

    }

    @Override
    public Mono<CapacityResponse> calculateDebtCapacity(CapacityRequest request) {
        return Mono.deferContextual(ctx -> {
            String token = ctx.get("authToken");
        return  client.post()
                        .uri(Constants.PATH_CAPACITY)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(CapacityResponse.class)
                        .timeout(Duration.ofSeconds(5))
                        .onErrorMap(throwable -> {
                            if (throwable instanceof WebClientResponseException) {
                                return new RuntimeException(throwable.getMessage(), throwable
                                );
                            }
                            return new RuntimeException("", throwable);
                        });
        });
    }


}
