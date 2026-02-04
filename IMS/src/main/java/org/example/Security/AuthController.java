package org.example.Security;

import org.example.DTO.Request.UserLoginRequest;
import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Users;
import org.example.Exception.ResourceNotFoundException;
import org.example.Repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersRepository usersRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;


    public AuthController(TokenService tokenService){
        this.tokenService = tokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequest request) {

        logger.info("Attempting to login with email: {}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = tokenService.generateToken(authentication);

        Users user = usersRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        logger.info("Successfully logged in user:{} (email: {})", user.getUsername(), user.getEmail() );
        return ResponseEntity.ok(response);

    }


}
