package com.moneymanger.moneytracker.config;

import com.moneymanger.moneytracker.security.JwtRequestFilter;
import com.moneymanger.moneytracker.service.ApplicationUserDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration {


    private final ApplicationUserDetailService applicationUserDetailService;
    private final JwtRequestFilter jwtRequestFilter;

      @Value("${money.manager.frontend.url:https://moneymanagerwebap.netlify.app}")
         private String frontUrl;
    public SecurityConfiguration(ApplicationUserDetailService applicationUserDetailService, JwtRequestFilter jwtRequestFilter) {
        this.applicationUserDetailService = applicationUserDetailService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults()) // use corsConfigurationSource bean
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF for API
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/status/**", "/health/**", "/login/**", "/register/**", "/activate/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // optional, can remove if using JWT only
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ CORS configuration
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Your React frontend origin
        configuration.setAllowedOriginPatterns(List.of(frontUrl));

        // Allowed HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow cookies / Authorization headers
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ✅ AuthenticationManager with DaoAuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(applicationUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}
