package co.com.pragma.powerup.usecase.loanapplication;

import co.com.pragma.powerup.model.exceptions.*;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.response.LoanApplicationListItem;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.loantype.LoanType;
import co.com.pragma.powerup.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
@Log4j2
@RequiredArgsConstructor
public class LoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public Mono<ResponseLoanApplication> createLoanApplication(LoanApplication loanApplication,String idCard, String token, String idCardFromToken){
        return getUserByIdCard(loanApplication, idCard, token, idCardFromToken)
                .flatMap(loanApp -> validateLoanType(loanApp.getIdLoanType()))
                .flatMap(loanType -> validateAmount(loanApplication, loanType))
                .flatMap(this::assignPendingStatus)
                .flatMap(this::saveApplication)
                .doOnSuccess(savedLoan ->
                        log.info(Constants.LOG_LA_CREATE_SUCCESSFUL,savedLoan.getLoanApplication().getIdLoanType() ))
                .doOnError(error ->
                        log.error(Constants.LOG_LA_CREATE_ERROR, error.getMessage()));
    }

    public Mono<PageResponse<LoanApplicationListItem>> execute(int page, int size, String filter) {
        log.info(Constants.LOG_FETCHING, page, size, filter);

        validateParameters(page, size, filter);

        return loanApplicationRepository.findPending(page, size, filter)
                .switchIfEmpty(Mono.error(new NoLoanApplicationsFoundException(
                        Constants.ERROR_NO_RESULTS)))
                .doOnSuccess(response -> log.info(
                        Constants.LOG_SUCCESS,
                        response.getItems().size()))
                .doOnError(this::handleRepositoryError);
    }

    private void validateParameters(int page, int size, String filter) {
        if (page < 0 || size <= 0)
            throw new InvalidPaginationParametersException(
                    Constants.ERROR_INVALID_PAGINATION);
        if (filter == null)
            throw new InvalidFilterException(
                    Constants.ERROR_INVALID_FILTER);
    }

    private void handleRepositoryError(Throwable error) {
        log.error(Constants.LOG_ERROR, error.getMessage());
        throw new LoanApplicationRepositoryException(
                Constants.ERROR_REPOSITORY, error);
    }

    private Mono<LoanApplication> getUserByIdCard(LoanApplication loanApplication, String idCard, String token,String idCardFromToken){
        return userRepository.getUserByIdCard(idCard, token)
                .switchIfEmpty(Mono.error(new UserNotFoundException(Constants.LOG_ERROR_GET_USER)))
                .flatMap(user -> {
                    if (!user.getIdCard().equals(idCardFromToken)) {
                        return Mono.error(new UserIdCardMismatchException(user.getIdCard(), idCardFromToken));
                    }
                    loanApplication.setEmail(user.getEmailAddress());
                    return Mono.just(loanApplication);
                });
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