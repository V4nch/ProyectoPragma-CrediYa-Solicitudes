package co.com.pragma.powerup.model.loanapplication.gateways;

import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
}
