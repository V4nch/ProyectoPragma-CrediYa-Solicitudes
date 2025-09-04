package co.com.pragma.powerup.model.user.gateways;

import co.com.pragma.powerup.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> getUserById(String idCard);
}
