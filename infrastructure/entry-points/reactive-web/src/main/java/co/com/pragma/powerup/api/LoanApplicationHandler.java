package co.com.pragma.powerup.api;

import co.com.pragma.powerup.api.exception.ErrorResponse;
import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.request.LoanApplicationRequest;
import co.com.pragma.powerup.model.loanapplication.request.UpdateLoanStatusRequest;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import co.com.pragma.powerup.model.loanapplication.response.ResponseLoanApplication;
import co.com.pragma.powerup.model.utils.Constants;
import co.com.pragma.powerup.usecase.loanapplication.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoanApplicationHandler {
    private final LoanApplicationUseCase loanApplicationUseCase;



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
        var exchange = request.exchange();
        String idCardFromToken = exchange.getAttribute("idCard");
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        return request.bodyToMono(LoanApplicationRequest.class)
            .doOnNext(laReq -> log.debug(Constants.LOG_RECEIVED_DATA, laReq))
            .flatMap(laReq -> loanApplicationUseCase.createLoanApplication(
                new LoanApplication(laReq.getAmount(),laReq.getTerm(),laReq.getIdLoanType()),
                        laReq.getIdCard(), idCardFromToken
            ))
            .doOnSuccess(la -> log.info(Constants.LOG_LOAN_APP_CREATED, la.getStatusLoanApplication()))
            .doOnError(error -> log.error(Constants.LOG_LOAN_APP_CREATION_ERROR, error.getMessage()))
            .flatMap(la -> ServerResponse.ok().bodyValue(la))
                .contextWrite(ctx -> ctx.put("authToken", authHeader != null ? authHeader: ""));
    }

    @Operation(
        summary =Constants.SUMMARY_GET_LOAN_APP,
        description =Constants.DESCRIPTION_GET_LOAN_APP,
        parameters = {
            @Parameter(
                name = "page",
                description = "Page number (starts from 0)",
                example = "0",
                required = false,
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "size",
                description = "Page size (number of items per page)",
                example = "10",
                required = false,
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "filter",
                description = "Search filter by applicant loan type name, email or status name",
                example = "",
                required = false,
                in = ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
            responseCode = Constants.CODE_200,
            description = Constants.RESPONSE_LOAN_APP_LISTED,
            content = @Content(
                schema = @Schema(implementation = PageResponse.class),
                examples = {
                    @ExampleObject(
                        name = Constants.EXAMPLE_LOAN_APP_LISTED_NAME,
                        value = Constants.EXAMPLE_LOAN_APP_LISTED_VALUE
                    )
                }
            )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_400,
                description = Constants.RESPONSE_BAD_REQUEST_GET,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name = Constants.EXAMPLE_INVALID_PAGINATION_NAME,
                            value = Constants.EXAMPLE_INVALID_PAGINATION_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_404,
                description = Constants.RESPONSE_NOT_FOUND_GET,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name = Constants.EXAMPLE_NO_LOAN_APPS_FOUND_NAME,
                            value = Constants.EXAMPLE_NO_LOAN_APPS_FOUND_VALUE
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = Constants.CODE_500,
                description = Constants.RESPONSE_INTERNAL_ERROR_GET,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                            name = Constants.EXAMPLE_SERVER_ERROR_NAME_GET,
                            value = Constants.EXAMPLE_SERVER_ERROR_VALUE_GET
                        )
                    }
                )
            )
        }
    )
    public Mono<ServerResponse> getLoanApplication(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String filter = request.queryParam("filter").orElse("");

        log.info(Constants.LOG_GET_LOAN_APP_REQUEST, page, size, filter);

        return loanApplicationUseCase.getLoanApp(page, size, filter)
                .flatMap(response -> {
                    log.info(Constants.LOG_GET_LOAN_APP_SUCCESS, response.getTotalItems());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnError(error -> log.error(Constants.LOG_GET_LOAN_APP_ERROR, error.getMessage(), error));
    }

    @Operation(
        summary =Constants.SUMMARY_UPDATE_LOAN_APP,
        description =Constants.DESCRIPTION_UPDATE_LOAN_APP,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = UpdateLoanStatusRequest.class),
                examples = {
                    @ExampleObject(
                        name =Constants.EXAMPLE_LOAN_APP_PUT_NAME,
                        value =Constants.EXAMPLE_LOAN_APP_PUT_VALUE
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = Constants.CODE_200,
                description = Constants.DESCRIPTION_UPDATE_LOAN_APP,
                content = @Content(
                    schema = @Schema(implementation = ResponseLoanApplication.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_LOAN_APP_PUT_RESPONSE_NAME,
                            value =Constants.EXAMPLE_LOAN_APP_PUT_RESPONSE_VALUE
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
                description = Constants.RESPONSE_NOT_FOUND_GET,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(
                                name = Constants.EXAMPLE_NO_LOAN_APPS_FOUND_NAME,
                                value = Constants.EXAMPLE_NO_LOAN_APPS_FOUND_VALUE
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
    public Mono<ServerResponse> putLoanApplication(ServerRequest request){


        return request.bodyToMono(UpdateLoanStatusRequest.class)
                .flatMap(loanApplicationUseCase::putLoanApp)
                .flatMap(updatedLoan -> {
                    log.info(Constants.LOG_UPDATE_LOAN_SUCCESS, updatedLoan.getStatusLoanApplication());
                    return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedLoan);})
                .doOnError(error -> log.error(Constants.LOG_UPDATE_LOAN_ERROR, error.getMessage(), error));

    }

}