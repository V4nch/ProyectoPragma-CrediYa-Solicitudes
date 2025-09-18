package co.com.pragma.powerup.usecase.debtcapacity;

import co.com.pragma.powerup.model.debtcapacity.PaymentPlan;
import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.debtcapacity.response.CapacityResponse;
import co.com.pragma.powerup.model.exceptions.InvalidParameterException;
import co.com.pragma.powerup.model.exceptions.UserNotFoundException;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.user.User;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DebtCapacityUseCase {

        private final LoanApplicationRepository loanApplicationRepository;
        private final UserRepository userRepository;
        private Double interestRate;

    private static final double RATIO = 0.35; // 35%

    public Mono<CapacityResponse> calculateCapacity(CapacityRequest request) {
        return Mono.zip(getUser(request.getIdCard()), getInterestRate(request))
                .flatMap(tuple -> processCapacityCalculation(request, tuple.getT1(), tuple.getT2()));
    }

    private Mono<User> getUser(String idCard) {
        return userRepository.getUserByIdCard(idCard)
                .switchIfEmpty(Mono.error(new UserNotFoundException(Constants.NOT_FOUND_USER)));
    }

    private Mono<Double> getInterestRate(CapacityRequest request) {
        interestRate = request.getInterestRate();
        return (request.getInterestRate() != null)
           ?  Mono.just(request.getInterestRate()):
             Mono.error(new InvalidParameterException(Constants.INTEREST_RATE_NEED_IT));
    }

    private Mono<CapacityResponse> processCapacityCalculation(CapacityRequest request, User user, Double newLoanAnnualInterest) {
        return loanApplicationRepository.findApprovedLoansByIdCard(user.getIdCard())
                .collectList()
                .map(existingLoans -> buildResponse(user, request, newLoanAnnualInterest, existingLoans));
    }

    private CapacityResponse buildResponse(User user, CapacityRequest request, Double interestRate, List<LoanApplication> existingLoans) {
        double calculateCurrentDebt = calculateCurrentDebt(existingLoans);
        double calculateMaxCapacity = calculateMaxCapacity(user);
        double availableCapacity = calculateMaxCapacity - calculateCurrentDebt;
        double newPayment = calculateMonthlyPayment(request.getAmount(), interestRate, request.getTerm());
        List<PaymentPlan> plan = generatePaymentPlan(request.getAmount(), interestRate, request.getTerm());

        return CapacityResponse.builder()
                .maxCapacity(round(calculateMaxCapacity))
                .currentMonthlyDebt(round(calculateCurrentDebt))
                .availableCapacity(round(availableCapacity))
                .newLoanPayment(round(newPayment))
                .paymentPlan(plan)
                .baseSalary(user.getBaseSalary())
                .build();
    }


    private double calculateCurrentDebt(List<LoanApplication> loans) {
        return loans.stream()
                .mapToDouble(this::calculateMonthlyPaymentFromLoan)
                .sum();
    }

    private double calculateMaxCapacity(User user) {
        return Double.parseDouble(user.getBaseSalary()) * RATIO;
    }

    private double calculateMonthlyPaymentFromLoan(LoanApplication la) {

        if (interestRate == null) interestRate = 0.0;
        return calculateMonthlyPayment(la.getAmount(), interestRate, la.getTerm());
    }

    /**
     * Formula de amortización:
     * cuota = P * (i * (1+i)^n) / ((1+i)^n - 1)
     * i = tasa mensual decimal (ej. 0.015 para 1.5%)
     */
    private double calculateMonthlyPayment(double principal, double annualInterestPercent, int months) {
        if (months <= 0) return 0.0;
        double monthlyRate = annualInterestPercent / 100.0 / 12.0;
        if (monthlyRate == 0.0) return principal / months;
        double pow = Math.pow(1 + monthlyRate, months);
        return principal * ((monthlyRate * pow) / (pow - 1));
    }

    private List<PaymentPlan> generatePaymentPlan(double principal, double annualInterestPercent, int months) {
        List<PaymentPlan> plan = new ArrayList<>();
        double monthlyRate = annualInterestPercent / 100.0 / 12.0;
        double loanPayment = calculateMonthlyPayment(principal, annualInterestPercent, months);


        for (int m = 1; m <= months; m++) {
            double interest = principal * monthlyRate;
            double principalPaid = loanPayment - interest;
            if (m == months) {
                principalPaid = principal;
                loanPayment = principalPaid + interest;
            }
            principal -= principalPaid;
            plan.add(PaymentPlan.builder()
                    .month(m)
                    .totalPayment(round(loanPayment))
                    .principal(round(principalPaid))
                    .interest(round(interest))
                    .remainingBalance(round(Math.max(principal, 0.0)))
                    .build());
        }
        return plan;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
