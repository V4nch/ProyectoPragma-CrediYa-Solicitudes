package co.com.pragma.powerup.usecase.loanapplication;

import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.exceptions.*;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.CapacityRestRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.NotificationQueueRepository;
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
    private final NotificationQueueRepository notificationQueueRepository;
    private final CapacityRestRepository capacityRestRepository;

    public Mono<ResponseLoanApplication> createLoanApplication(LoanApplication loanApplication,String idCard, String idCardFromToken){
        return getUserByIdCard(loanApplication, idCard, idCardFromToken)
                .flatMap(loanApp -> validateLoanType(loanApp.getIdLoanType())
                        .flatMap(loanType -> attachLoanType(loanApp, loanType)))
                .flatMap(this::validateAmount)
                .flatMap(this::assignPendingStatus)
                .flatMap(this::saveLoanApp)
                .flatMap(la -> handleAutomaticValidation(la,idCard));
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



    public Mono<ResponseLoanApplication> putLoanApp(UpdateLoanStatusRequest request) {
        return loanApplicationRepository.findById(request.getLoanId())
            .switchIfEmpty(Mono.error(new NoLoanApplicationsFoundException(Constants.ERROR_NOT_FOUND_LOAN)))
            .flatMap(loan -> statusRepository.findByName(request.getNewStatus()))
            .switchIfEmpty(Mono.error(new StatusNotFoundException(Constants.STATUS_NOT_FOUND)))
            .flatMap(statusId ->
                    loanApplicationRepository.updateStatus(request.getLoanId(), statusId.getIdStatus()))
            .flatMap(updatedLoan ->
                    notificationQueueRepository.sendNotification(
                        String.format("{\"type\": \"%s\" ,\"loanId\": %d, \"email\": \"%s\", \"status\": \"%s\"}",
                            Constants.LOAN_STATE,request.getLoanId(), updatedLoan.getEmail(), request.getNewStatus()
                        )
                    )
                    .thenReturn(
                        new ResponseLoanApplication(updatedLoan, request.getNewStatus())
                    )
            );
    }
    private Mono<LoanApplication> attachLoanType(LoanApplication loanApp, LoanType loanType) {
        loanApp.setLoanType(loanType);
        return Mono.just(loanApp);
    }

    private Mono<ResponseLoanApplication> handleAutomaticValidation(LoanApplication newLoan, String idCard) {
        if (requiresAutomaticValidation(newLoan)) {
            return capacityRestRepository.calculateDebtCapacity(
                            new CapacityRequest(newLoan.getLoanId(), idCard, newLoan.getAmount(), newLoan.getTerm(), newLoan.getLoanType().getInterestRate()))
                    .thenReturn(new ResponseLoanApplication(newLoan, "En validacion"));
        }
        return Mono.just(new ResponseLoanApplication(newLoan, "pendiente de revision"));
    }

    private Mono<LoanApplication> saveLoanApp(LoanApplication loanApp){
        return  loanApplicationRepository.save(loanApp)
                .flatMap(la -> {
                    loanApp.setLoanId(la.getLoanId());
                    return Mono.just(loanApp);
                });
    }

    private boolean requiresAutomaticValidation(LoanApplication loan) {
        return loan.getLoanType() != null
                && loan.getLoanType().isAutomaticValidation();
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

    private Mono<LoanApplication> validateAmount(LoanApplication loanApplication) {
        if (loanApplication.getAmount() < loanApplication.getLoanType().getMinimumAmount() ||
                loanApplication.getAmount() > loanApplication.getLoanType().getMaximumAmount()) {
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
}