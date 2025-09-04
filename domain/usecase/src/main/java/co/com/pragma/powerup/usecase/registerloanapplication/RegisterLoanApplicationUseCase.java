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
import co.com.pragma.powerup.model.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
@Log4j2
@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;

    public Mono<ResponseLoanApplication> createLoanApplication(LoanApplication loanApplication){
        return validateLoanType(loanApplication.getIdLoanType())
                .flatMap(loanType -> validateAmount(loanApplication, loanType))
                .flatMap(this::assignPendingStatus)
                .flatMap(this::saveApplication)
                .doOnSuccess(savedLoan ->
                        log.info(Constants.LOG_LA_CREATE_SUCCESSFUL,savedLoan.getLoanApplication().getIdLoanType() ))
                .doOnError(error ->
                        log.error(Constants.LOG_LA_CREATE_ERROR, error.getMessage()));
    }

    private Mono<LoanType> validateLoanType(Long idLoanType) {
        return loanTypeRepository.findById(idLoanType)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(Constants.LOAN_TYPE_NOT_FOUND_MESSAGE)));
    }

    private Mono<LoanApplication> validateAmount(LoanApplication loanApplication, LoanType loanType) {
        if (loanApplication.getAmount() < loanType.getMinimumAmount() ||
                loanApplication.getAmount() > loanType.getMaximumAmount()) {
            return Mono.error(new AmountOutOfRangeException(Constants.LOAN_AMOUNT_OUT_RANGE_MESSAGE));
        }
        return Mono.just(loanApplication);
    }

    private Mono<LoanApplication> assignPendingStatus(LoanApplication loanApplication) {
        return statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)
                .switchIfEmpty(Mono.error(new StatusNotFoundException(Constants.STATUS_NOT_FOUND_MESSAGE)))
                .map(status -> {
                    loanApplication.setIdStatus(status.getIdStatus());
                    return loanApplication;
                });
    }

    private Mono<ResponseLoanApplication> saveApplication(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication)
                    .map(la -> new ResponseLoanApplication(la, Constants.STATUS_PENDING_REVIEW)
                );
    }
}