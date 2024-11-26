package com.rogosha.TaskManager.Controllers;

import com.rogosha.TaskManager.Models.User;
import com.rogosha.TaskManager.Repositories.UserRepository;
import com.rogosha.TaskManager.Security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@Tag(name = "Security controller", description = "Controller for registration and authentication")
@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtCore jwtCore;

    @Operation(summary = "Registration", description = "Allows to register by email, password and role")
    @PostMapping("/signup")
    ResponseEntity<?> signup(@Parameter(description = "Sign up request with email, password and role parameters") @RequestBody SignUpRequest signUpRequest){
        if (userRepository.findById(signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CHOOSE DIFFERENT NAME");
        }

        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(signUpRequest.getEmail()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT EMAIL");
        }

        User user = new User();
        String hashed = passwordEncoder.encode(signUpRequest.getPassword());
        user.setId(signUpRequest.getEmail());
        user.setPassword(hashed);
        user.setRole((signUpRequest.getRole()));
        userRepository.save(user);
        return ResponseEntity.ok("Success");
    }

    @Operation(summary = "Authentication", description = "Allows to authenticate by email and password. Return JWT-token")
    @PostMapping("/signin")
    ResponseEntity<?> signin(@Parameter(description = "Sign up request with email and password parameters") @RequestBody SignInRequest signInRequest){
        Authentication authentication = null;
        try {
            //String hashed = passwordEncoder.encode(signInRequest.getPassword());
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

}
