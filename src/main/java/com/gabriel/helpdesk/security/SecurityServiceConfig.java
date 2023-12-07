package com.gabriel.helpdesk.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.repository.PessoaRepository;

@Service
public class SecurityServiceConfig implements UserDetailsService {

	@Autowired
	private PessoaRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Pessoa> user = repository.findByEmail(email);
		if (user.isPresent()) {
			return new SecurityDetails(user.get().getId(), user.get().getEmail(), user.get().getSenha(),
					user.get().getPerfis());
		}
		throw new UsernameNotFoundException(email);
	}

}