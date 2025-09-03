package co.com.pragma.powerup.usecase.registerloanapplication;

import co.com.pragma.powerup.model.exceptions.*;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.loantype.LoanType;
import co.com.pragma.powerup.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.powerup.model.status.Status;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import co.com.pragma.powerup.model.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterLoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private RegisterLoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        statusRepository = mock(StatusRepository.class);

        useCase = new RegisterLoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                statusRepository
        );
    }

    @Test
    void createLoanApplication_success() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(10L);
        loanApplication.setAmount(5000.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(10L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        Status pendingStatus = new Status();
        pendingStatus.setIdStatus(99L);
        pendingStatus.setName(Constants.STATUS_PENDING_REVIEW);

        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)).thenReturn(Mono.just(pendingStatus));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createLoanApplication(loanApplication))
                .expectNextMatches(response -> {
                    return response.getLoanApplication().getAmount().equals(5000.0)
                            && response.getStatusLoanApplication().equals(Constants.STATUS_PENDING_REVIEW);
                })
                .verifyComplete();
        verify(statusRepository).findByName(Constants.STATUS_PENDING_REVIEW);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void createLoanApplication_loanTypeNotFound() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(20L);

        when(loanTypeRepository.findById(20L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication(loanApplication))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void testConstantsClassLoads() {
        assertNotNull(Constants.STATUS_PENDING_REVIEW);
        assertEquals("application/json", Constants.CONTENT_TYPE);
    }
    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void createLoanApplication_amountOutOfRange() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(30L);
        loanApplication.setAmount(50000.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(30L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        when(loanTypeRepository.findById(30L)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.createLoanApplication(loanApplication))
                .expectError(AmountOutOfRangeException.class)
                .verify();
        StepVerifier.create(useCase.createLoanApplication(loanApplication))
                .expectErrorSatisfies(error -> {
                    assertEquals(Constants.LOAN_AMOUNT_OUT_RANGE_MESSAGE, error.getMessage());
                })
                .verify();
    }

    @Test
    void createLoanApplication_statusNotFound() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(40L);
        loanApplication.setAmount(2000.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(40L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        when(loanTypeRepository.findById(40L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication(loanApplication))
                .expectError(StatusNotFoundException.class)
                .verify();
    }
}