package com.bucott.store.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bucott.store.dto.auth.LoginRequestDTO;
import com.bucott.store.dto.auth.LoginResponseDTO;
import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.service.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication", 
    description = "Authentication and authorization endpoints"
)
public class AuthController {
    // logging dependency
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    UserDetailsServiceImpl userDetailsService;

    public AuthController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    
    @Operation(
        summary = "Login",
        description = "Authenticate a user and return a JWT token",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Successful login",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        log.info("Login attempt for user: {}", loginRequestDTO.getUsernameOrEmail());
        
        try {
            var loginResponse = userDetailsService.authenticate(loginRequestDTO, response);
            return ResponseEntity.ok(loginResponse);
        } catch(Exception e) {
            Map<String, Object> errorResponse = Map.of("error", e.getMessage());
            errorResponse.put("message", "Login failed: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    

    // register endpoint
    @Operation(
        summary = "Register",
        description = "Register a new user",
        responses = {
            @ApiResponse(
                responseCode = "200", description = "Successful registration",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegisterResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request")
        }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO, HttpServletResponse response) {
        log.info("Register attempt for user: {}", registerRequestDTO.getUsername());
        
        try {
            var registerResponse = userDetailsService.register(registerRequestDTO, response);
            return ResponseEntity.ok(registerResponse);
        } catch(Exception e) {
            Map<String, Object> errorResponse = Map.of("error", e.getMessage());
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }            
    
    // NO FUNCIONA, ASEGURAR QUE LA COOKIE Y LA SESION PERSISTEN
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        log.info("Logout attempt");

        userDetailsService.logout(response);
        
        Map<String, Object> logoutResponse= new HashMap<>();
        logoutResponse.put("message", "Logged out successfully");

        return ResponseEntity.ok(logoutResponse);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        var userInfo = userDetailsService.getCurrentUser(request);
        return ResponseEntity.ok(userInfo);
    }

    // NO FUNCIONA, ASAEGURAR QUE LA COOKIE PERSISTE
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        boolean isValid = userDetailsService.validateTokenFromCookie(request.getCookies());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("authenticated", isValid);

        return ResponseEntity.ok(responseBody);
    }
    
}
