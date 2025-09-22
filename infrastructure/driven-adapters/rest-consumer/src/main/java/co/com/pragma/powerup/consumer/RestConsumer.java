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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RestConsumer implements UserRepository, CapacityRestRepository {

    private final WebClient userWebClient;
    private final WebClient loanWebClient;

    public RestConsumer(
            @Qualifier("userWebClient") WebClient userWebClient,
            @Qualifier("loanWebClient") WebClient loanWebClient
    ) {
        this.userWebClient = userWebClient;
        this.loanWebClient = loanWebClient;
    }

    @Override
    public Mono<User> getUserByIdCard(String idCard) {
        return Mono.deferContextual(ctx -> {
            String token = ctx.get("authToken");
            return userWebClient.get()
                    .uri(Constants.PATH_USER, idCard)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .map(UserMapper::toDomain)
                    .onErrorMap(throwable ->
                            new RuntimeException(Constants.LOG_ERROR_GET_USER, throwable)
                    );
        });
    }

    @Override
    public Mono<CapacityResponse> calculateDebtCapacity(CapacityRequest request) {
        return Mono.deferContextual(ctx -> {
            String token = ctx.get("authToken");
            return loanWebClient.post()
                    .uri("/calcular-capacidad")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(CapacityResponse.class)
                    .onErrorMap(throwable ->
                            new RuntimeException("", throwable)
                    );
        });
    }


}
