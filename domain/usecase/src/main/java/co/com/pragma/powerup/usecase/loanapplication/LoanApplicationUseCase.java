package co.com.pragma.powerup.usecase.loanapplication;

import co.com.pragma.powerup.model.exceptions.*;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.request.UpdateLoanStatusRequest;
import co.com.pragma.powerup.model.loanapplication.response.LoanApplicationListItem;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.loantype.LoanType;
import co.com.pragma.powerup.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import lombok.RequiredArgsConstructor;


import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public Mono<ResponseLoanApplication> createLoanApplication(LoanApplication loanApplication,String idCard, String idCardFromToken){
        return getUserByIdCard(loanApplication, idCard,idCardFromToken)
                .flatMap(loanApp -> validateLoanType(loanApp.getIdLoanType()))
                .flatMap(loanType -> validateAmount(loanApplication, loanType))
                .flatMap(this::assignPendingStatus)
                .flatMap(this::saveApplication);
    }

    public Mono<PageResponse<LoanApplicationListItem>> getLoanApp(int page, int size, String filter) {

        if (page < 0 || size <= 0) {
            return Mono.error(new InvalidPaginationParametersException(
                    Constants.ERROR_INVALID_PAGINATION));
        }

        return loanApplicationRepository.findPending(page, size, filter)
                .switchIfEmpty(Mono.error(new NoLoanApplicationsFoundException(
                        Constants.ERROR_NO_RESULTS)));
    }

    public Mono<LoanApplication> putLoanApp(UpdateLoanStatusRequest request) {
        return loanApplicationRepository.findById(request.getLoanId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Solicitud no encontrada")))
            .flatMap(loan -> statusRepository.findByName(request.getNewStatus())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Estado inválido")))
            .flatMap(statusId ->
                    loanApplicationRepository.updateStatus(loan.getIdLoanType(), statusId.getIdStatus()))
                );
    }

    private Mono<LoanApplication> getUserByIdCard(LoanApplication loanApplication,
                                                  String idCard,String idCardFromToken){
        return userRepository.getUserByIdCard(idCard)
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