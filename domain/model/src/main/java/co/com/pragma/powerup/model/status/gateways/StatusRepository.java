package co.com.pragma.powerup.model.status.gateways;


import co.com.pragma.powerup.model.status.Status;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Status> findByName(String name);
    Mono<Status> findById(Long idStatus);
}
