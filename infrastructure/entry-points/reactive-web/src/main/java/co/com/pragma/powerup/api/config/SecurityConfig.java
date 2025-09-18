package co.com.pragma.powerup.api.config;

import co.com.pragma.powerup.api.auth.JwtAuthFilter;
import co.com.pragma.powerup.model.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthFilter jwtAuthFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.POST, Constants.PATH_CAPACITY).permitAll()
                        .pathMatchers(
                                Constants.SWAGGER_INDEX,
                                Constants.SWAGGER_UI_HTML,
                                Constants.SWAGGER_UI_ALL,
                                Constants.V3_API_DOCS,
                                Constants.WEBJARS
                        ).permitAll()
                        .pathMatchers(HttpMethod.POST, Constants.PATH_LOAN_APPLICATION).hasRole(Constants.ROLE_CLIENT)
                        .pathMatchers(HttpMethod.GET, Constants.PATH_LOAN_APPLICATION).hasAnyRole(Constants.ROLE_ADVISOR)
                        .pathMatchers(HttpMethod.PUT, Constants.PATH_LOAN_APPLICATION).hasAnyRole(Constants.ROLE_ADVISOR)
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
