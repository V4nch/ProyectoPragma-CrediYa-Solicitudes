package co.com.pragma.powerup.model.utils;

public class Constants {
    public static final String STATUS_PENDING_REVIEW = "Pendiente de revision";
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String LOAN_AMOUNT_OUT_RANGE_MESSAGE = "Loan amount is outside the allowed range for this loan type";
    public static final String STATUS_NOT_FOUND_MESSAGE = "Status not found";
    public static final String LOAN_TYPE_NOT_FOUND_MESSAGE= "Loan type not found";
    public static final String ERROR_INVALID_PAGINATION = "Invalid pagination parameters.";
    public static final String ERROR_NO_RESULTS = "No pending loan applications found.";
    // ---------------------------
    // HTTP
    // ---------------------------
    public static final String PATH_LOAN_APPLICATION = "/api/v1/solicitud";
    public static final String CONTENT_TYPE = "application/json";
    public static final String NAME_FUNCTION = "createLoanApplication";
    public static final String GET_NAME_FUNCTION = "getLoanApplication";
    public static final String API_CREDIYA = "API CrediYa";
    public static final String VERSION_1 = "1.0";
    public static final String LOAN_APP_DESCRIPTION = "API for managing loan applications in CrediYa";
    public static final String CODE_200 = "200";
    public static final String CODE_400 = "400";
    public static final String CODE_404 = "404";
    public static final String CODE_500 = "500";
    public static final String PATH_USER = "/usuarios/{id}";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SWAGGER_UI_ALL = "/swagger-ui/**";
    public static final String V3_API_DOCS = "/v3/api-docs/**";
    public static final String WEBJARS = "/webjars/**";
    public static final String SWAGGER_INDEX = "/swagger-ui/index.html";
    public static final String ROLE_CLIENT = "cliente";
    public static final String ROLE_ADVISOR = "asesor";
    public static final String BEARER_AUTH = "BearerAuth";
    public static final String BEARER = "bearer";
    public static final String JWT = "JWT";
    public static final String AUTHORIZATION = "Authorization";
    public static final String USER_ID_CARD_MISMATCH = "ID Card mismatch: provided '%s' does not match expected '%s'.";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String ADAPTER_URL = "${adapter.restconsumer.url}";
    public static final String ADAPTER_TIMEOUT ="${adapter.restconsumer.timeout}";



    // ---------------------------
    // LOGGING MESSAGES
    // ---------------------------
    public static final String LOG_LOAN_APP_RECEIVED = "Loan request received";
    public static final String LOG_AMOUNT_OUT_OF_RANGE = "AMOUNT OUT OF RANGE: {}";
    public static final String LOG_LOAN_TYPE_NOT_FOUND = "Error, loan type not found: {}";
    public static final String LOG_STATUS_NOT_FOUND = "Error, status not found: {}";
    public static final String LOG_DB_INTEGRITY_ERROR  = "INTEGRITY ERROR IN DB: {}";
    public static final String LOG_DATA_ACCESS_ERROR = "Data access error: {}";
    public static final String LOG_UNEXPECTED_ERROR = "Unexpected error: {}";
    public static final String LOG_RECEIVED_DATA = "Received data: {}";
    public static final String LOG_LOAN_APP_CREATED = "Loan application created with status: {}";
    public static final String LOG_LOAN_APP_CREATION_ERROR = "Error creating loan application: {}";
    public static final String LOG_ERROR_USER = "Error: ";
    public static final String LOG_ERROR_GET_USER = "Error user not found";
    public static final String LOG_ERROR_GETTING_USER = "Error get user: {}";
    public static final String LOG_USER_ID_CARD_MISMATCH = "ID Card mismatch: provided  does not match expected: {}";
    public static final String LOG_NO_LOAN_APP_ERROR = "No loan app found: {}";
    public static final String LOG_INVALID_PAGINATION_ERROR = "Invalid pagination: {}";
    public static final String LOG_GET_LOAN_APP_REQUEST = "GetLoanApplication request received - page: {}, size: {}, filter: {}";
    public static final String LOG_GET_LOAN_APP_SUCCESS = "GetLoanApplication successful - totalItems: {}";
    public static final String LOG_GET_LOAN_APP_ERROR   = "Error in GetLoanApplication: {}";
    // ---------------------------
    // ROUTER OPERATION
    // ---------------------------
    public static final String SUMMARY_REGISTER_LOAN_APP = "Register loan application";
    public static final String DESCRIPTION_REGISTER_LOAN_APP = "Allows registering a new application";
    public static final String SUMMARY_GET_LOAN_APP = "GET loan application";
    public static final String DESCRIPTION_GET_LOAN_APP = "Allows getting a loan application";
    // ---------------------------
    // HTTP ERROR MESSAGES
    // ---------------------------
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String LOAN_TYPE_NOT_FOUND = "LOAN_TYPE_NOT_FOUND";
    public static final String STATUS_NOT_FOUND = "STATUS_NOT_FOUND";
    public static final String DB_VIOLATION_MESSAGE = "Database constrain violation";
    public static final String DB_ACCESS_ERROR = "Database access error";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String UNEXPECTED_ERROR = "An unexpected error has occurred";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    // ---------------------------
    // SUCCESS RESPONSES
    // ---------------------------
    public static final String RESPONSE_LOAN_APP_REGISTERED = "Loan application registered";
    public static final String EXAMPLE_LOAN_APP_REGISTERED_NAME = "Registered loan application";
    public static final String EXAMPLE_LOAN_APP_REGISTERED_VALUE = """
            {
                  "loanApplication": {
                        "email" : "IvanM@gm.com",
                        "amount" : 1500000,
                        "term": 36,
                        "idLoanType": 1
                        },
                  "statusLoanApplication": "Pendiente de revision"
            }
            """;
    public static final String EXAMPLE_LOAN_APP_REQUEST_VALUE = """
            {
                "idCard" : "1094123123",
                "amount" : 1500000,
                "term": 36,
                "idLoanType": 1
            }
            """;
    // ---------------------------
    // ERROR RESPONSES
    // ---------------------------
    public static final String RESPONSE_BAD_REQUEST = "Invalid data";
    public static final String RESPONSE_NOT_FOUND = "Loan type not found";
    public static final String RESPONSE_INTERNAL_ERROR = "Unexpected error";

    // ---------------------------
    // ERROR EXAMPLES
    // ---------------------------
    public static final String EXAMPLE_AMOUNT_OUT_OF_RANGE_NAME = "Amount out of range error";
    public static final String EXAMPLE_AMOUNT_OUT_OF_RANGE_VALUE = """
            {
              "code": "BAD_REQUEST",
              "message": "Loan amount is outside the allowed range for this loan type"
            }
            """;

    public static final String EXAMPLE_STATUS_NOT_FOUND_NAME = "Status not found error";
    public static final String EXAMPLE_STATUS_NOT_FOUND_VALUE = """
            {
              "code": "NOT_FOUND",
              "message": "Status not found"
            }
            """;
    public static final String EXAMPLE_LOAN_TYPE_NOT_FOUND_NAME = "Loan Type not found error";
    public static final String EXAMPLE_LOAN_TYPE_NOT_FOUND_VALUE = """
            {
              "code": "NOT_FOUND",
              "message": "Loan type not found"
            }
            """;

    public static final String EXAMPLE_SERVER_ERROR_NAME = "Server error";
    public static final String EXAMPLE_SERVER_ERROR_VALUE = """
            {
              "code": "INTERNAL_SERVER_ERROR",
              "message": "Unexpected error occurred"
            }
            """;
    // Response descriptions
    public static final String RESPONSE_LOAN_APP_LISTED = "Loan applications successfully listed";
    public static final String RESPONSE_BAD_REQUEST_GET = "Invalid request parameters";
    public static final String RESPONSE_NOT_FOUND_GET = "No loan applications found";
    public static final String RESPONSE_INTERNAL_ERROR_GET = "Unexpected server error";

    // Example names
    public static final String EXAMPLE_LOAN_APP_LISTED_NAME = "LoanApplicationListResponseExample";
    public static final String EXAMPLE_INVALID_PAGINATION_NAME = "InvalidPaginationExample";
    public static final String EXAMPLE_NO_LOAN_APPS_FOUND_NAME = "NoLoanApplicationsFoundExample";
    public static final String EXAMPLE_SERVER_ERROR_NAME_GET = "ServerErrorExample";

    // Example values
    public static final String EXAMPLE_LOAN_APP_LISTED_VALUE = """
        {
          "page": 0,
          "size": 2,
          "totalItems": 12,
          "items": [
            {
              "amount": 5000.0,
              "term": 12,
              "email": "john.doe@mail.com",
              "name": "John Doe",
              "loanTypeName": "Personal Loan",
              "interestRate": 5.5,
              "statusName": "PENDING",
              "baseSalary": 2500.0,
              "monthlyAmount": 450.0
            },
            {
              "amount": 10000.0,
              "term": 24,
              "email": "jane.smith@mail.com",
              "name": "Jane Smith",
              "loanTypeName": "Car Loan",
              "interestRate": 6.0,
              "statusName": "REVIEW",
              "baseSalary": 4000.0,
              "monthlyAmount": 500.0
            }
          ]
        }
        """;

    public static final String EXAMPLE_INVALID_PAGINATION_VALUE = """
        {
          "error": "InvalidPaginationParametersException",
          "message": "Pagination parameters are invalid. Page must be >= 0 and size must be > 0."
        }
        """;

    public static final String EXAMPLE_NO_LOAN_APPS_FOUND_VALUE = """
        {
          "error": "NoLoanApplicationsFoundException",
          "message": "No loan applications found for the given filter."
        }
        """;

    public static final String EXAMPLE_SERVER_ERROR_VALUE_GET = """
        {
          "error": "LoanApplicationRepositoryException",
          "message": "Unexpected error while querying the repository."
        }
        """;

}
