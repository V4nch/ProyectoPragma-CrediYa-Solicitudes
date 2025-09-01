package co.com.pragma.powerup.usecase.registerloanapplication;

import co.com.pragma.powerup.model.exceptions.AmountOutOfRangeException;
import co.com.pragma.powerup.model.exceptions.LoanTypeNotFoundException;
import co.com.pragma.powerup.model.exceptions.StatusNotFoundException;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.loantype.LoanType;
import co.com.pragma.powerup.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    public Mono<ResponseLoanApplication> CreateLoanApplication(LoanApplication loanApplication){
        return validateLoanType(loanApplication.getIdLoanType())
                .flatMap(loanType -> validateAmount(loanApplication, loanType))
                .flatMap(validRequest -> assignPendingStatus(validRequest))
                .flatMap(this::saveApplication)
                .onErrorResume(this::handleError);
    }

    private Mono<LoanType> validateLoanType(Long idLoanType) {
        return loanTypeRepository.findById(idLoanType)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()));
    }

    private Mono<LoanApplication> validateAmount(LoanApplication loanApplication, LoanType loanType) {
        if (loanApplication.getAmount() < loanType.getMinimumAmount() ||
                loanApplication.getAmount() > loanType.getMaximumAmount()) {
            return Mono.error(new AmountOutOfRangeException());
        }
        return Mono.just(loanApplication);
    }

    private Mono<LoanApplication> assignPendingStatus(LoanApplication loanApplication) {
        return statusRepository.findByName(LoanConstants.STATUS_PENDING_REVIEW)
                .switchIfEmpty(Mono.error(new StatusNotFoundException()))
                .map(status -> {
                    loanApplication.setIdStatus(status.getIdStatus());
                    return loanApplication;
                });
    }

    private Mono<ResponseLoanApplication> saveApplication(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication)
                .flatMap(saved ->
                        statusRepository.findById(saved.getIdStatus())
                                .map(status -> new ResponseLoanApplication(saved, status.getName()))
                );
    }

    private Mono<ResponseLoanApplication> handleError(Throwable e) {
        return Mono.just(new ResponseLoanApplication(null, e.getMessage()));
    }
}