package co.com.pragma.powerup.api.config;

import co.com.pragma.powerup.api.auth.JwtAuthFilter;
import co.com.pragma.powerup.model.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthFilter jwtAuthFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // <- Deshabilita Basic Auth
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // <- Deshabilita Form Login
                .authorizeExchange(ex -> ex
                        // Swagger público
                        .pathMatchers(
                                "/swagger-ui/index.html",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        // Endpoints protegidos
                        .pathMatchers(Constants.PATH_LOAN_APPLICATION).hasRole("cliente")
                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
