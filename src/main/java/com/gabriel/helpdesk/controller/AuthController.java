package com.gabriel.helpdesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabriel.helpdesk.security.JWTUtil;
import com.gabriel.helpdesk.security.dto.CredenciaisDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody CredenciaisDto dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenProvider.generateToken((String) auth.getPrincipal());
        return ResponseEntity.ok(token);
    }
}
