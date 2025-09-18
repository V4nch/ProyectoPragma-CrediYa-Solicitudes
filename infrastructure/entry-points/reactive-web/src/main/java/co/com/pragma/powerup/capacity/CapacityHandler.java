package co.com.pragma.powerup.capacity;

import co.com.pragma.powerup.api.exception.ErrorResponse;
import co.com.pragma.powerup.model.debtcapacity.request.CapacityRequest;
import co.com.pragma.powerup.model.debtcapacity.response.CapacityResponse;
import co.com.pragma.powerup.model.utils.Constants;
import co.com.pragma.powerup.usecase.debtcapacity.DebtCapacityUseCase;
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
public class CapacityHandler {

    private final DebtCapacityUseCase debtCapacityUseCase;


    @Operation(
        summary =Constants.SUMMARY_CALCULATE_CAPACITY_APP,
        description =Constants.DESCRIPTION_CALCULATE_CAPACITY_APP,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = CapacityRequest.class),
                examples = {
                    @ExampleObject(
                        name =Constants.EXAMPLE_CALCULATE_CAPACITY_NAME,
                        value =Constants.EXAMPLE_CALCULATE_CAPACITY_REQUEST_VALUE
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = Constants.CODE_200,
                description = Constants.RESPONSE_CALCULATE_CAPACITY,
                content = @Content(
                    schema = @Schema(implementation = CapacityResponse.class),
                    examples = {
                        @ExampleObject(
                            name =Constants.EXAMPLE_CALCULATE_CAPACITY_NAME,
                            value =Constants.EXAMPLE_CALCULATE_CAPACITY_RESPONSE_VALUE
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
                            name =Constants.ERROR_INVALID_PARAMETER,
                            value =Constants.EXAMPLE_INVALID_PARAMETER_VALUE
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
                            name =Constants.EXAMPLE_USER_NOT_FOUND_NAME,
                            value =Constants.EXAMPLE_USER_NOT_FOUND_VALUE
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
    public Mono<ServerResponse> calculateDebtCapacity(ServerRequest request) {
        log.info(Constants.LOG_CAPACITY_RECEIVED);
        return request.bodyToMono(CapacityRequest.class)
                .doOnNext(laReq -> log.debug(Constants.LOG_RECEIVED_DATA, laReq))
                .flatMap(debtCapacityUseCase::calculateCapacity)
                .doOnSuccess(la -> log.info(Constants.LOG_CAPACITY_CREATED))
                .doOnError(error -> log.error(Constants.LOG_CAPACITY_ERROR))
                .flatMap(la -> ServerResponse.ok().bodyValue(la));
    }
}
