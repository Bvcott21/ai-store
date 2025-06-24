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
import jakarta.validation.Valid;

import com.bucott.store.dto.auth.LoginRequestDTO;
import com.bucott.store.dto.auth.LoginResponseDTO;
import com.bucott.store.dto.auth.RegisterRequestDTO;
import com.bucott.store.dto.auth.RegisterResponseDTO;
import com.bucott.store.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication", 
    description = "Authentication and authorization endpoints"
)
public class AuthController {
    // logging dependency
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for user: {}", loginRequestDTO.getUsernameOrEmail());
        var loginResponse = authService.authenticate(loginRequestDTO);
        return ResponseEntity.ok(loginResponse);
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
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("Register attempt for user: {}", registerRequestDTO.getUsername());
        var registerResponse = authService.register(registerRequestDTO);
        return ResponseEntity.ok(registerResponse);
    }            
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout attempt");
        
        Map<String, Object> logoutResponse= new HashMap<>();
        logoutResponse.put("message", "Logged out successfully - please remove token from client");

        return ResponseEntity.ok(logoutResponse);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            var userInfo = authService.getUserInfoByUsername(username);
            return ResponseEntity.ok(userInfo);
        }
        
        var userInfo = authService.getUserInfoByUsername(null);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isValid = authentication != null && 
                         authentication.isAuthenticated() && 
                         !"anonymousUser".equals(authentication.getName());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("authenticated", isValid);

        return ResponseEntity.ok(responseBody);
    }
    
}
