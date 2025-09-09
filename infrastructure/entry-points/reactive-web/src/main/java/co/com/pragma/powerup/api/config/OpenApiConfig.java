package co.com.pragma.powerup.api.config;


import co.com.pragma.powerup.model.utils.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solicitudesOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(Constants.API_CREDIYA)
                .version(Constants.VERSION_1)
                .description(Constants.LOAN_APP_DESCRIPTION))
                .components(new Components()
                        .addSecuritySchemes(Constants.BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(Constants.BEARER)
                                        .bearerFormat(Constants.JWT)
                                        .in(SecurityScheme.In.HEADER)
                                        .name(Constants.AUTHORIZATION)
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(Constants.BEARER_AUTH));
    }
}
