package com.app.stock.controller;

import com.app.stock.dto.LoginRequestDTO;
import com.app.stock.dto.OTPVerificationRequestDTO;
import com.app.stock.dto.UserRegistrationDTO;
import com.app.stock.model.User;
import com.app.stock.repository.UserRepository;
import com.app.stock.security.JwtUtil;
import com.app.stock.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setVerified(false);
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtp(otp);

        userRepository.save(user);
        emailService.sendOtpEmail(dto.getEmail(), otp);

        return ResponseEntity.ok("User registered. Please check your email for OTP.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPVerificationRequestDTO dto) {
        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getOtp().equals(dto.getOtp())) {
                user.setVerified(true);
                user.setOtp(null);
                userRepository.save(user);
                return ResponseEntity.ok("Account verified successfully.");
            }
        }
        return ResponseEntity.badRequest().body("Invalid email or OTP.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
        if (userOptional.isEmpty() || !userOptional.get().isVerified()) {
            return ResponseEntity.status(403).body("User is not verified.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        String token = jwtUtil.generateToken(dto.getEmail());
        return ResponseEntity.ok().body(token);
    }
}
