package co.com.pragma.powerup.model.utils;

public class Constants {
    public static final String STATUS_PENDING_REVIEW = "Pendiente de revisión";
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String LOAN_AMOUNT_OUT_RANGE_MESSAGE = "Loan amount is outside the allowed range for this loan type";
    public static final String STATUS_NOT_FOUND_MESSAGE = "Status not found";
    public static final String LOAN_TYPE_NOT_FOUND_MESSAGE= "Loan type not found";
    // ---------------------------
    // HTTP
    // ---------------------------
    public static final String PATH_LOAN_APPLICATION = "/api/v1/solicitud";
    public static final String LOG_LA_CREATE_SUCCESSFUL = "Loan Application successfully created: {}";
    public static final String LOG_LA_CREATE_ERROR = "Error creating Loan Application: {}";
    public static final String CONTENT_TYPE = "application/json";
    public static final String NAME_FUNCTION = "createLoanApplication";
    public static final String API_CREDIYA = "API CrediYa";
    public static final String VERSION_1 = "1.0";
    public static final String LOAN_APP_DESCRIPTION = "API for managing loan applications in CrediYa";
    public static final String CODE_200 = "200";
    public static final String CODE_400 = "400";
    public static final String CODE_404 = "404";
    public static final String CODE_500 = "500";



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
    // ---------------------------
    // ROUTER OPERATION
    // ---------------------------
    public static final String SUMMARY_REGISTER_LOAN_APP = "Register loan application";
    public static final String DESCRIPTION_REGISTER_LOAN_APP = "Allows registering a new application";
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
                  "statusLoanApplication": "Pendiente de revisión"
            }
            """;
    public static final String EXAMPLE_LOAN_APP_REQUEST_VALUE = """
            {
                "email" : "IvanM@gm.com",
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

}
