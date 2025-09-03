package co.com.pragma.powerup.api;

import co.com.pragma.powerup.api.exception.ErrorResponse;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.utils.Constants;
import co.com.pragma.powerup.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoanApplicationHandler {
    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Operation(
        summary =Constants.SUMMARY_REGISTER_LOAN_APP,
        description =Constants.DESCRIPTION_REGISTER_LOAN_APP,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = LoanApplication.class),
                examples = {
                    @ExampleObject(
                        name =Constants.EXAMPLE_LOAN_APP_REGISTERED_NAME,
                        value =Constants.EXAMPLE_LOAN_APP_REQUEST_VALUE
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = Constants.CODE_200,
                description = Constants.RESPONSE_LOAN_APP_REGISTERED,
                content = @Content(
                    schema = @Schema(implementation = ResponseLoanApplication.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_LOAN_APP_REGISTERED_NAME,
                            value =Constants.EXAMPLE_LOAN_APP_REGISTERED_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_400,
                description =Constants.RESPONSE_BAD_REQUEST,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_AMOUNT_OUT_OF_RANGE_NAME,
                            value =Constants.EXAMPLE_AMOUNT_OUT_OF_RANGE_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_404,
                description =Constants.RESPONSE_NOT_FOUND,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_STATUS_NOT_FOUND_NAME,
                            value =Constants.EXAMPLE_STATUS_NOT_FOUND_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_404,
                description =Constants.RESPONSE_NOT_FOUND,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_LOAN_TYPE_NOT_FOUND_NAME,
                            value =Constants.EXAMPLE_LOAN_TYPE_NOT_FOUND_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_500,
                description =Constants.RESPONSE_INTERNAL_ERROR,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_SERVER_ERROR_NAME,
                            value =Constants.EXAMPLE_SERVER_ERROR_VALUE
                        )
                    }
                )
            )
        }
    )
    public Mono<ServerResponse> createLoanApplication(ServerRequest request) {
        log.info(Constants.LOG_LOAN_APP_RECEIVED);

        return request.bodyToMono(LoanApplication.class)
                .doOnNext(la -> log.debug(Constants.LOG_RECEIVED_DATA, la))
                .flatMap(registerLoanApplicationUseCase::createLoanApplication)
                .doOnSuccess(la -> log.info(Constants.LOG_LOAN_APP_CREATED, la.getStatusLoanApplication()))
                .doOnError(error -> log.error(Constants.LOG_LOAN_APP_CREATION_ERROR, error.getMessage()))
                .flatMap(la -> ServerResponse.ok().bodyValue(la));
    }

}