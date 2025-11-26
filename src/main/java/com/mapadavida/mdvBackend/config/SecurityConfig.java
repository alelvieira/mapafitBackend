package com.mapadavida.mdvBackend.config;

import com.mapadavida.mdvBackend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.http.HttpMethod; // permit GET on tipos endpoints

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Rotas públicas (sem autenticação)
                        .requestMatchers(
                                "/usuarios/login",
                                "/usuarios/cadastrar",
                                "/usuarios/forgot-password",
                                "/usuarios/validate-reset-token",
                                "/usuarios/reset-password",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Allow CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Allow public GET access to tipos endpoints used by the frontend (and permit error path)
                        .requestMatchers(HttpMethod.GET, "/locais/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rotas restritas a admin (exemplo seu)
                        .requestMatchers("/api/cache/**").hasRole("ADMIN")

                        // 🔒 Qualquer outra rota exige login via JWT
                        .anyRequest().authenticated()
                )
                // Filtro JWT antes do filtro padrão do Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Em produção, substitua por origens específicas
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "authorization", "content-type", "x-auth-token", "x-requested-with", "accept"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for password hashing in production
        return new BCryptPasswordEncoder();
    }
}


// return http
//         .csrf(csrf -> csrf.disable())
//        .cors(Customizer.withDefaults()) // ✅ CORS habilitado
//        .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/login", "/usuarios", "/usuarios/login", "/local/geocode", "/local/geocode/", "/error").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/local/", "/usuarios/").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/local", "/local/", "/usuarios/", "/usuarios").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/local/", "/usuarios/").permitAll()
//                        .requestMatchers(HttpMethod.DELETE, "/local/", "/usuarios/", "/usuarios").permitAll()
//                        .anyRequest().authenticated()
//                )
//                        .build();
//    }