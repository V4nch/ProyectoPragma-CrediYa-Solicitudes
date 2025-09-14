package co.com.pragma.powerup.api;

import co.com.pragma.powerup.model.utils.Constants;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = Constants.PATH_LOAN_APPLICATION,
                    method = {RequestMethod.POST},
                    produces = { Constants.CONTENT_TYPE },
                    consumes = { Constants.CONTENT_TYPE },
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = Constants.NAME_FUNCTION
            ),
            @RouterOperation(
                    path = Constants.PATH_LOAN_APPLICATION,
                    method = {RequestMethod.GET},
                    produces = { Constants.CONTENT_TYPE },
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = Constants.GET_NAME_FUNCTION
            ),
            @RouterOperation(
                    path = Constants.PATH_LOAN_APPLICATION,
                    method = {RequestMethod.PUT},
                    produces = { Constants.CONTENT_TYPE },
                    consumes = { Constants.CONTENT_TYPE },
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = Constants.PUT_NAME_FUNCTION
            )
    })
    public RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler handler) {
        return route(POST(Constants.PATH_LOAN_APPLICATION), handler::createLoanApplication)
                .andRoute(GET(Constants.PATH_LOAN_APPLICATION), handler::getLoanApplication)
                .andRoute(PUT(Constants.PATH_LOAN_APPLICATION), handler::putLoanApplication);

    }
}
