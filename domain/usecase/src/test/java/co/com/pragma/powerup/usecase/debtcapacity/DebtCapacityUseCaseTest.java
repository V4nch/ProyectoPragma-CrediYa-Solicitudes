package co.com.pragma.powerup.usecase.debtcapacity;

import co.com.pragma.powerup.model.debtcapacity.PaymentPlan;
import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.exceptions.InvalidParameterException;
import co.com.pragma.powerup.model.exceptions.UserNotFoundException;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.NotificationQueueRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.ValidateQueueRepository;
import co.com.pragma.powerup.model.user.User;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DebtCapacityUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private UserRepository userRepository;
    private ValidateQueueRepository validateQueueRepository;
    private NotificationQueueRepository notificationQueueRepository;

    private DebtCapacityUseCase useCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        userRepository = mock(UserRepository.class);
        validateQueueRepository = mock(ValidateQueueRepository.class);
        notificationQueueRepository = mock(NotificationQueueRepository.class);

        useCase = new DebtCapacityUseCase(
                loanApplicationRepository,
                userRepository,
                validateQueueRepository,
                notificationQueueRepository
        );
    }

    // -------------------------------------------------------------------------
    // calculateCapacity
    // -------------------------------------------------------------------------

    @Test
    void calculateCapacity_successful() {
        CapacityRequest request = CapacityRequest.builder()
                .idCard("123")
                .amount(10000.0)
                .term(12)
                .loanId(1L)
                .interestRate(12.0)
                .build();

        User user = new User();
        user.setIdCard("123");
        user.setBaseSalary("5000");

        LoanApplication approvedLoan = new LoanApplication();
        approvedLoan.setAmount(2000.0);
        approvedLoan.setTerm(10);

        when(userRepository.getUserByIdCard("123")).thenReturn(Mono.just(user));
        when(loanApplicationRepository.findApprovedLoansByIdCard("123"))
                .thenReturn(Flux.just(approvedLoan));
        when(validateQueueRepository.sendValidation(anyString())).thenReturn(Mono.empty());
        when(notificationQueueRepository.sendNotification(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.calculateCapacity(request))
                .assertNext(response -> {
                    assertEquals(1L, response.getLoanId());
                    assertEquals(5000 * 0.35, response.getMaxCapacity(), 0.1);
                    assertTrue(response.getNewLoanPayment() > 0);
                    assertFalse(response.getPaymentPlan().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void calculateCapacity_userNotFound() {
        CapacityRequest request = CapacityRequest.builder()
                .idCard("999")
                .amount(5000.0)
                .term(12)
                .interestRate(10.0)
                .build();

        when(userRepository.getUserByIdCard("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.calculateCapacity(request))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void calculateCapacity_interestRateMissing() {
        CapacityRequest request = CapacityRequest.builder()
                .idCard("123")
                .amount(5000.0)
                .term(12)
                .loanId(5L)
                .build();

        User user = new User();
        user.setIdCard("123");

        when(userRepository.getUserByIdCard("123")).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.calculateCapacity(request))
                .expectError(InvalidParameterException.class)
                .verify();
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares internos
    // -------------------------------------------------------------------------

    @Test
    void calculateMonthlyPayment_withInterest() {
        double cuota = invokeCalculateMonthlyPayment(10000.0, 12.0, 12);
        assertTrue(cuota > 0);
    }

    @Test
    void calculateMonthlyPayment_withoutInterest() {
        double cuota = invokeCalculateMonthlyPayment(1200.0, 0.0, 12);
        assertEquals(100.0, cuota, 0.01);
    }

    @Test
    void calculateMonthlyPayment_invalidMonths() {
        double cuota = invokeCalculateMonthlyPayment(10000.0, 10.0, 0);
        assertEquals(0.0, cuota, 0.01);
    }

    @Test
    void generatePaymentPlan_createsCorrectNumberOfMonths() {
        List<PaymentPlan> plan = invokeGeneratePaymentPlan(1200.0, 12.0, 12);
        assertEquals(12, plan.size());
        assertEquals(1, plan.get(0).getMonth());
        assertEquals(12, plan.get(11).getMonth());
    }

    // -------------------------------------------------------------------------
    // Utils privados para llamar métodos package-private
    // -------------------------------------------------------------------------

    private double invokeCalculateMonthlyPayment(double principal, double annualRate, int months) {
        try {
            var method = DebtCapacityUseCase.class
                    .getDeclaredMethod("calculateMonthlyPayment", double.class, double.class, int.class);
            method.setAccessible(true);
            return (double) method.invoke(useCase, principal, annualRate, months);
        } catch (Exception e) {
            fail("Error invoking calculateMonthlyPayment: " + e.getMessage());
            return 0;
        }
    }

    private List<PaymentPlan> invokeGeneratePaymentPlan(double principal, double annualRate, int months) {
        try {
            var method = DebtCapacityUseCase.class
                    .getDeclaredMethod("generatePaymentPlan", double.class, double.class, int.class);
            method.setAccessible(true);
            return (List<PaymentPlan>) method.invoke(useCase, principal, annualRate, months);
        } catch (Exception e) {
            fail("Error invoking generatePaymentPlan: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
