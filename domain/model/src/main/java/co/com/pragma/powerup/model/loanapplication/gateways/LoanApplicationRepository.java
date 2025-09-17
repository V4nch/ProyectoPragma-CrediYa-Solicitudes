package co.com.pragma.powerup.model.loanapplication.gateways;

import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.response.LoanApplicationListItem;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<PageResponse<LoanApplicationListItem>> findPending(int page, int size, String filter);
    Mono<LoanApplication> findById(Long id);
    Mono<LoanApplication> updateStatus(Long loanId, Long idStatus);
    Flux<LoanApplication> findApprovedLoansByIdCard(String idCard);

}
