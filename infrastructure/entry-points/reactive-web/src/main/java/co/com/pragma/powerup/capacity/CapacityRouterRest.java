package co.com.pragma.powerup.capacity;

import co.com.pragma.powerup.api.LoanApplicationHandler;
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
public class CapacityRouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = Constants.PATH_CAPACITY,
                    method = {RequestMethod.POST},
                    produces = { Constants.CONTENT_TYPE },
                    consumes = { Constants.CONTENT_TYPE },
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = Constants.NAME_FUNCTION_CAPACITY
            )
    })
    public RouterFunction<ServerResponse> routerFunction(CapacityHandler handler) {
        return route(POST(Constants.PATH_CAPACITY), handler::calculateDebtCapacity);

    }
}
