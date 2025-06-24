package com.bucott.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // Set allowed headers
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        
        // Set whether credentials are allowed
        configuration.setAllowCredentials(allowCredentials);
        
        // Set preflight cache duration
        configuration.setMaxAge(maxAge);
        
        // Allow common headers needed for JWT authentication
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}