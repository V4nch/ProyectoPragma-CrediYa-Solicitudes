package co.com.pragma.powerup.model.loanapplication.gateways;

import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.debtcapacity.response.CapacityResponse;
import reactor.core.publisher.Mono;

public interface CapacityRestRepository {
    Mono<CapacityResponse> calculateDebtCapacity(CapacityRequest request);
}
