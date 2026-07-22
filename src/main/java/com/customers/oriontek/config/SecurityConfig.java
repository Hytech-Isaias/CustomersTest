package com.customers.oriontek.config;

import com.customers.oriontek.infrastructure.logging.CorrelationIdFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final ApiKeyAuthenticationFilter apiKeyFilter;
        private final CorrelationIdFilter correlationIdFilter;

        public SecurityConfig(final ApiKeyAuthenticationFilter apiKeyFilter,
                        final CorrelationIdFilter correlationIdFilter) {
                this.apiKeyFilter = apiKeyFilter;
                this.correlationIdFilter = correlationIdFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/actuator/health",
                                                                "/actuator/info")
                                                .permitAll()
                                                // Role-Based Access Control
                                                .requestMatchers(HttpMethod.POST, "/api/v1/customers/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/customers/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
