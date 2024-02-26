package com.gabriel.helpdesk.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.repository.ClienteRepository;
import com.gabriel.helpdesk.repository.PessoaRepository;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

	@InjectMocks
	private ClienteService service;

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private BCryptPasswordEncoder encoder;

	private Cliente cliente;

	@BeforeEach
	void setUp() {
		cliente = new Cliente(null, "Albert Einstein", "111.661.890-74", "einstein@mail.com", encoder.encode("123"));
	}

	@Test
	void testFindById() {
		Integer id = 1;
		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

		Cliente foundCliente = service.findById(id);

		Assertions.assertNotNull(foundCliente);
		Assertions.assertEquals(cliente.getId(), foundCliente.getId());
	}

	@Test
	void testFindAll() {
		List<Cliente> clienteList = new ArrayList<>();
		clienteList.add(cliente);
		Mockito.when(clienteRepository.findAll()).thenReturn(clienteList);

		List<Cliente> foundClientes = service.findAll();

		Assertions.assertNotNull(foundClientes);
		Assertions.assertFalse(foundClientes.isEmpty());
		Assertions.assertEquals(1, foundClientes.size());
	}

	@Test
	void testCreate() {
		ClienteDto dto = new ClienteDto();
		dto.setNome(cliente.getNome());
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha(cliente.getSenha());

		Mockito.when(encoder.encode(cliente.getSenha())).thenReturn("hashedPassword");
		Mockito.when(pessoaRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
		Mockito.when(pessoaRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
		Mockito.when(clienteRepository.save(Mockito.any(Cliente.class))).thenAnswer(invocation -> {
			Cliente savedCliente = invocation.getArgument(0);
			savedCliente.setId(1); // Assigning an ID for the saved cliente
			return savedCliente;
		});

		Cliente createdCliente = service.create(dto);

		Assertions.assertNotNull(createdCliente);
		Assertions.assertEquals(cliente.getNome(), createdCliente.getNome());
	}

	@Test
	void testUpdate() {
		Integer id = 1;
		ClienteDto dto = new ClienteDto();
		dto.setId(id);
		dto.setNome("Updated Name");
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha("newPassword");

		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
		Mockito.when(encoder.encode(dto.getSenha())).thenReturn("hashedPassword");
		Mockito.when(pessoaRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
		Mockito.when(pessoaRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
		Mockito.when(clienteRepository.save(Mockito.any(Cliente.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Cliente updatedCliente = service.update(id, dto);

		Assertions.assertNotNull(updatedCliente);
		Assertions.assertEquals(dto.getNome(), updatedCliente.getNome());
	}

	@Test
	void testDelete() {
		Integer id = 1;
		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
		Mockito.doNothing().when(clienteRepository).deleteById(id);

		Assertions.assertDoesNotThrow(() -> service.delete(id));
	}
}
