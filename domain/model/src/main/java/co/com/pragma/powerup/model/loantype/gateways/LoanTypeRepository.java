package co.com.pragma.powerup.model.loantype.gateways;

import co.com.pragma.powerup.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<LoanType> findById(Long idLoanType);
}
