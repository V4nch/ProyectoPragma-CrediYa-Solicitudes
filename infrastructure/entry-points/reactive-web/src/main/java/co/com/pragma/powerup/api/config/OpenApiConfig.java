package co.com.pragma.powerup.api.config;


import co.com.pragma.powerup.model.utils.Constants;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solicitudesOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(Constants.API_CREDIYA)
                .version(Constants.VERSION_1)
                .description(Constants.LOAN_APP_DESCRIPTION));
    }
}
