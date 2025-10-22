package com.mapadavida.mdvBackend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // ✅ CORS habilitado
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/usuarios", "/usuarios/login", "/local/geocode", "/local/geocode/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permite OPTIONS para todos
                        .requestMatchers(HttpMethod.GET, "/local/**", "/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/local", "/local/", "/usuarios/**", "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/local/**", "/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/local/**", "/usuarios/**", "/usuarios").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
