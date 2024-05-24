package com.gabriel.helpdesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.dto.PessoaDto;
import com.gabriel.helpdesk.repository.PessoaRepository;
import com.gabriel.helpdesk.security.JWTUtil;
import com.gabriel.helpdesk.security.dto.CredenciaisDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private JWTUtil tokenProvider;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody CredenciaisDto dto) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());
		var auth = authenticationManager.authenticate(usernamePassword);
		var token = tokenProvider.generateToken((String) auth.getPrincipal());
		return ResponseEntity.ok(token);
	}

	@GetMapping("/profile")
	public ResponseEntity<PessoaDto> getAuthenticatedInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		Pessoa pessoa = pessoaRepository.findByEmail(currentPrincipalName)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		return ResponseEntity.ok(new PessoaDto(pessoa));
	}

	@PutMapping("/profile")
	public ResponseEntity<PessoaDto> update(@Valid @RequestBody PessoaDto pessoaDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		Pessoa pessoa = pessoaRepository.findByEmail(currentPrincipalName)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		if (!pessoaDto.getSenha().equals(pessoa.getSenha())) {
			pessoa.setSenha(encoder.encode(pessoaDto.getSenha()));
		}

		pessoa.setNome(pessoaDto.getNome());
		pessoa.setCpf(pessoaDto.getCpf());
		pessoa.setEmail(pessoaDto.getEmail());

		Pessoa updatedPessoa = pessoaRepository.save(pessoa);
		return ResponseEntity.ok(new PessoaDto(updatedPessoa));
	}
}