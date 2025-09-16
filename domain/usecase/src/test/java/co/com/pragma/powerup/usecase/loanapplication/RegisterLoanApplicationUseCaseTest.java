package co.com.pragma.powerup.usecase.loanapplication;

import co.com.pragma.powerup.model.exceptions.*;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.gateways.NotificationQueueRepository;
import co.com.pragma.powerup.model.loanapplication.request.UpdateLoanStatusRequest;
import co.com.pragma.powerup.model.loanapplication.response.LoanApplicationListItem;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import co.com.pragma.powerup.model.loantype.LoanType;
import co.com.pragma.powerup.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.powerup.model.status.Status;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import co.com.pragma.powerup.model.user.User;
import co.com.pragma.powerup.model.user.gateways.UserRepository;
import co.com.pragma.powerup.model.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterLoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private LoanApplicationUseCase useCase;
    private UserRepository userRepository;
    private NotificationQueueRepository sqsRepository;


    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        statusRepository = mock(StatusRepository.class);
        userRepository = mock(UserRepository.class);
        sqsRepository = mock(NotificationQueueRepository.class);

        useCase = new LoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                statusRepository,
                userRepository,
                sqsRepository
        );
    }


    @Test
    void createLoanApplication_setsUserEmail() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(80L);
        loanApplication.setAmount(3000.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(80L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        Status status = new Status();
        status.setIdStatus(1L);
        status.setName(Constants.STATUS_PENDING_REVIEW);

        var user = new User();
        user.setIdCard("789"); // necesario por la comparación en getUserByIdCard
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("789")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(80L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"789","789"))
                .expectNextMatches(response ->
                        "test@mail.com".equals(response.getLoanApplication().getEmail()))
                .verifyComplete();
    }

    @Test
    void createLoanApplication_statusNotFound_messageCheck() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(90L);
        loanApplication.setAmount(2000.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(90L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        User user = new User();
        user.setIdCard("111"); // debe coincidir con idCardFromToken
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("111")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(90L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"111","111"))
                .expectErrorSatisfies(error ->
                        assertEquals(Constants.STATUS_NOT_FOUND_MESSAGE, error.getMessage()))
                .verify();
    }

    @Test
    void createLoanApplication_loanTypeNotFound() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(20L);

        User user = new User();
        user.setIdCard("222");
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("222")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(20L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"222","222"))
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

        User user = new User();
        user.setIdCard("333");
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("333")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(30L)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"333","333"))
                .expectError(AmountOutOfRangeException.class)
                .verify();

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"333","333"))
                .expectErrorSatisfies(error ->
                        assertEquals(Constants.LOAN_AMOUNT_OUT_RANGE_MESSAGE, error.getMessage()))
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

        User user = new User();
        user.setIdCard("444");
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("444")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(40L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.STATUS_PENDING_REVIEW)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"444","444"))
                .expectError(StatusNotFoundException.class)
                .verify();
    }
    @Test
    void createLoanApplication_amountBelowMinimum() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(50L);
        loanApplication.setAmount(500.0);

        LoanType loanType = new LoanType();
        loanType.setIdLoanType(50L);
        loanType.setMinimumAmount(1000.0);
        loanType.setMaximumAmount(10000.0);

        User user = new User();
        user.setIdCard("555");
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("555")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(50L)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"555","555"))
                .expectError(AmountOutOfRangeException.class)
                .verify();
    }

    @Test
    void createLoanApplication_userNotFound() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(60L);
        loanApplication.setAmount(3000.0);

        when(userRepository.getUserByIdCard("123"))
                .thenReturn(Mono.error(new UserNotFoundException(Constants.LOG_ERROR_GET_USER)));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"123","123"))
                .expectError(UserNotFoundException.class)
                .verify();
    }
    @Test
    void createLoanApplication_userRepoThrowsUnexpectedError() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(70L);
        loanApplication.setAmount(3000.0);

        when(userRepository.getUserByIdCard("456"))
                .thenReturn(Mono.error(new RuntimeException("DB connection error")));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"456","456"))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof RuntimeException);
                    assertEquals("DB connection error", error.getMessage());
                })
                .verify();
    }
    @Test
    void testGettersAndSetters() {
        LoanApplication loanApplication = new LoanApplication();

        loanApplication.setEmail("test@mail.com");
        loanApplication.setAmount(5000.0);
        loanApplication.setTerm(12);
        loanApplication.setIdLoanType(1L);
        loanApplication.setIdStatus(2L);

        assertEquals("test@mail.com", loanApplication.getEmail());
        assertEquals(5000.0, loanApplication.getAmount());
        assertEquals(12, loanApplication.getTerm());
        assertEquals(1L, loanApplication.getIdLoanType());
        assertEquals(2L, loanApplication.getIdStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LoanApplication loanApplication = new LoanApplication(
                "test@mail.com", 3000.0, 24, 5L, 10L
        );

        assertEquals("test@mail.com", loanApplication.getEmail());
        assertEquals(3000.0, loanApplication.getAmount());
        assertEquals(24, loanApplication.getTerm());
        assertEquals(5L, loanApplication.getIdLoanType());
        assertEquals(10L, loanApplication.getIdStatus());
    }

    @Test
    void testCustomConstructor() {
        LoanApplication loanApplication = new LoanApplication(1500.0, 6, 7L);

        assertNull(loanApplication.getEmail());
        assertEquals(1500.0, loanApplication.getAmount());
        assertEquals(6, loanApplication.getTerm());
        assertEquals(7L, loanApplication.getIdLoanType());
        assertNull(loanApplication.getIdStatus());
    }

    @Test
    void testBuilder() {
        LoanApplication loanApplication = LoanApplication.builder()
                .email("builder@mail.com")
                .amount(7500.0)
                .term(18)
                .idLoanType(20L)
                .idStatus(30L)
                .build();

        assertEquals("builder@mail.com", loanApplication.getEmail());
        assertEquals(7500.0, loanApplication.getAmount());
        assertEquals(18, loanApplication.getTerm());
        assertEquals(20L, loanApplication.getIdLoanType());
        assertEquals(30L, loanApplication.getIdStatus());
    }

    @Test
    void testToBuilder() {
        LoanApplication original = LoanApplication.builder()
                .email("original@mail.com")
                .amount(1000.0)
                .term(12)
                .idLoanType(1L)
                .idStatus(2L)
                .build();

        LoanApplication modified = original.toBuilder()
                .amount(2000.0)
                .build();

        assertEquals("original@mail.com", modified.getEmail());
        assertEquals(2000.0, modified.getAmount());
        assertEquals(12, modified.getTerm());
        assertEquals(1L, modified.getIdLoanType());
        assertEquals(2L, modified.getIdStatus());
    }

    @Test
    void testToStringContainsClassName() {
        LoanApplication loanApplication = LoanApplication.builder()
                .email("string@mail.com")
                .amount(1234.0)
                .term(10)
                .idLoanType(3L)
                .idStatus(4L)
                .build();

        String toString = loanApplication.toString();
        assertTrue(toString.contains("LoanApplication"));
        assertTrue(toString.contains("string@mail.com"));
    }
    @Test
    void createLoanApplication_userIdCardMismatch() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdLoanType(60L);
        loanApplication.setAmount(3000.0);

        LoanType loanType = LoanType.builder()
                .idLoanType(60L)
                .minimumAmount(1000.0)
                .maximumAmount(10000.0)
                .build();

        User user = new User();
        user.setIdCard("999"); // idCard del usuario
        user.setEmailAddress("test@mail.com");

        when(userRepository.getUserByIdCard("999")).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(60L)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.createLoanApplication(loanApplication,"999","123")) // idCardFromToken distinto
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof UserIdCardMismatchException);
                    assertEquals("ID Card mismatch: provided '999' does not match expected '123'.", error.getMessage());
                })
                .verify();
    }
    @Test
    void getLoanApp_successful() {
        LoanApplicationListItem item = LoanApplicationListItem.builder()
                .amount(5000.0)
                .term(12)
                .email("test@mail.com")
                .userName("Test User")
                .loanTypeName("Personal Loan")
                .interestRate(10.5)
                .statusName("Pendiente")
                .baseSalary("3000")
                .monthlyRequestedAmount("450")
                .build();

        PageResponse<LoanApplicationListItem> pageResponse =
                new PageResponse<>(0, 10, 1, 1, List.of(item));

        when(loanApplicationRepository.findPending(0, 10, "test"))
                .thenReturn(Mono.just(pageResponse));

        StepVerifier.create(useCase.getLoanApp(0, 10, "test"))
                .expectNextMatches(resp ->
                        resp.getItems().size() == 1 &&
                                "test@mail.com".equals(resp.getItems().get(0).getEmail()))
                .verifyComplete();
    }

    @Test
    void getLoanApp_invalidPageParameters() {
        StepVerifier.create(useCase.getLoanApp(-1, 10, "test"))
                .expectError(InvalidPaginationParametersException.class)
                .verify();

        StepVerifier.create(useCase.getLoanApp(0, 0, "test"))
                .expectError(InvalidPaginationParametersException.class)
                .verify();
    }

    @Test
    void getLoanApp_noResultsFound() {
        when(loanApplicationRepository.findPending(0, 10, "nothing"))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.getLoanApp(0, 10, "nothing"))
                .expectError(NoLoanApplicationsFoundException.class)
                .verify();
    }
    @Test
    void putLoanApp_successful() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest(1L, "Aprobado");

        LoanApplication loan = LoanApplication.builder()
                .email("loan@mail.com")
                .idLoanType(1L)
                .idStatus(1L)
                .build();

        Status status = new Status();
        status.setIdStatus(2L);
        status.setName("Aprobado");

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loan));
        when(statusRepository.findByName("Aprobado")).thenReturn(Mono.just(status));
        when(loanApplicationRepository.updateStatus(1L, 2L)).thenReturn(Mono.just(loan));
        when(sqsRepository.send(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.putLoanApp(request))
                .expectNextMatches(resp ->
                        resp.getLoanApplication().getEmail().equals("loan@mail.com")
                                && "Aprobado".equals(resp.getStatusLoanApplication())
                )
                .verifyComplete();
    }

    @Test
    void putLoanApp_loanNotFound() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest(99L, "Rechazado");

        when(loanApplicationRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.putLoanApp(request))
                .expectError(NoLoanApplicationsFoundException.class)
                .verify();
    }

    @Test
    void putLoanApp_statusNotFound() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest(1L, "Rechazado");

        LoanApplication loan = LoanApplication.builder().email("loan@mail.com").build();

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loan));
        when(statusRepository.findByName("Rechazado")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.putLoanApp(request))
                .expectError(StatusNotFoundException.class)
                .verify();
    }

    @Test
    void putLoanApp_updateStatusFails() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest(1L, "Rechazado");

        LoanApplication loan = LoanApplication.builder().email("loan@mail.com").build();

        Status status = new Status();
        status.setIdStatus(2L);
        status.setName("Rechazado");

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loan));
        when(statusRepository.findByName("Rechazado")).thenReturn(Mono.just(status));
        when(loanApplicationRepository.updateStatus(1L, 2L))
                .thenReturn(Mono.error(new RuntimeException("DB update failed")));

        StepVerifier.create(useCase.putLoanApp(request))
                .expectErrorMatches(err -> err instanceof RuntimeException &&
                        err.getMessage().equals("DB update failed"))
                .verify();
    }

    @Test
    void putLoanApp_notificationFails() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest(1L, "Aprobado");

        LoanApplication loan = LoanApplication.builder().email("loan@mail.com").build();

        Status status = new Status();
        status.setIdStatus(2L);
        status.setName("Aprobado");

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loan));
        when(statusRepository.findByName("Aprobado")).thenReturn(Mono.just(status));
        when(loanApplicationRepository.updateStatus(1L, 2L)).thenReturn(Mono.just(loan));
        when(sqsRepository.send(anyString()))
                .thenReturn(Mono.error(new RuntimeException("SQS unavailable")));

        StepVerifier.create(useCase.putLoanApp(request))
                .expectErrorMatches(err -> err instanceof RuntimeException &&
                        err.getMessage().equals("SQS unavailable"))
                .verify();
    }

}