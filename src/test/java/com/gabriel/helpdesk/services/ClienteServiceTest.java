package com.gabriel.helpdesk.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Chamado;
import com.gabriel.helpdesk.services.utils.Validators;
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
	@Mock
	private Cliente cliente;
	@Mock
	private Validators validators;

	@BeforeEach
	void setUp() {
		cliente = new Cliente(null, "Albert Einstein", "111.661.890-74", "einstein@mail.com", encoder.encode("123"));
	}

	@Test
	void whenTestFindByIdIsSucess() {
		Integer id = 1;
		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

		Cliente foundCliente = service.findById(id);

		Assertions.assertNotNull(foundCliente);
		Assertions.assertEquals(cliente.getId(), foundCliente.getId());
		Assertions.assertEquals(cliente.getNome(), foundCliente.getNome());
		Assertions.assertEquals(cliente.getCpf(), foundCliente.getCpf());
		Assertions.assertEquals(cliente.getEmail(), foundCliente.getEmail());
	}

	@Test
	void whenTestFindByIdThrowsException() {
		Integer id = 1;
		String errorMessage = "Cliente Não Encontrado";

		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.empty());

		Exception exception = Assertions.assertThrows(ObjectNotFoundExceptions.class, () -> {
			service.findById(id);
		});

		Assertions.assertEquals(errorMessage, exception.getMessage());
	}

	@Test
	void whenTestfindByEmailIsSucess() {
		String email = cliente.getEmail();
		Mockito.when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente).get());

		Cliente foundCliente = service.findByEmail(email);

		Assertions.assertNotNull(foundCliente);
		Assertions.assertEquals(cliente.getId(), foundCliente.getId());
		Assertions.assertEquals(cliente.getNome(), foundCliente.getNome());
		Assertions.assertEquals(cliente.getCpf(), foundCliente.getCpf());
		Assertions.assertEquals(cliente.getEmail(), foundCliente.getEmail());
	}

	@Test
	void whenTestfindByEmailThrowsException() {
		String invalidEmail = "emailinvalido";

		Mockito.when(clienteRepository.findByEmail(invalidEmail)).thenReturn(null);
		Exception exception = Assertions.assertThrows(ObjectNotFoundExceptions.class, () -> {
			service.findByEmail(invalidEmail);
		});
		Assertions.assertEquals("Cliente Não Encontrado", exception.getMessage());
	}


	@Test
	void whenTestFindAllIsSuccess() {
		List<Cliente> clienteList = new ArrayList<>();
		clienteList.add(cliente);
		Mockito.when(clienteRepository.findAll()).thenReturn(clienteList);

		List<Cliente> foundClientes = service.findAll();

		Assertions.assertNotNull(foundClientes);
		Assertions.assertFalse(foundClientes.isEmpty());
		Assertions.assertEquals(1, foundClientes.size());
		Assertions.assertEquals(cliente.getId(), foundClientes.get(0).getId());
		Assertions.assertEquals(cliente.getNome(), foundClientes.get(0).getNome());
		Assertions.assertEquals(cliente.getCpf(), foundClientes.get(0).getCpf());
		Assertions.assertEquals(cliente.getEmail(), foundClientes.get(0).getEmail());
	}

	@Test
	void whenTestCreateClientIsSuccess() {
		ClienteDto dto = new ClienteDto();
		dto.setNome(cliente.getNome());
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha(cliente.getSenha());

		Mockito.doNothing().when(validators).validaCpfEEmail(dto);
		Mockito.when(encoder.encode(cliente.getSenha())).thenReturn("hashedPassword");
		Mockito.when(clienteRepository.save(Mockito.any(Cliente.class))).thenAnswer(invocation -> {
			Cliente savedCliente = invocation.getArgument(0);
			savedCliente.setId(1);
			return savedCliente;
		});
		Cliente createdCliente = service.create(dto);
		Assertions.assertNotNull(createdCliente);
		Assertions.assertEquals(cliente.getNome(), createdCliente.getNome());
	}
	@Test
	void whenTestCreateClientFailsDueToValidation() {
		ClienteDto dto = new ClienteDto();
		dto.setNome(cliente.getNome());
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha(cliente.getSenha());

		// Simula uma falha na validação
		Mockito.doThrow(new DataIntegrityViolationException("CPF ou Email já cadastrados"))
				.when(validators).validaCpfEEmail(dto);

		Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			service.create(dto);
		});

		Assertions.assertEquals("CPF ou Email já cadastrados", exception.getMessage());
	}

	@Test
	void whenTestUpdateClientIsSucess() {
		Integer id = 1;
		ClienteDto dto = new ClienteDto();
		dto.setId(id);
		dto.setNome("Updated Name");
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha("newPassword");

		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
		Mockito.when(encoder.encode(dto.getSenha())).thenReturn("hashedPassword");
		Mockito.when(clienteRepository.save(Mockito.any(Cliente.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Cliente updatedCliente = service.update(id, dto);

		Assertions.assertNotNull(updatedCliente);
		Assertions.assertEquals(dto.getNome(), updatedCliente.getNome());
	}

	@Test
	void whenTestUpdateClientFailsDueToValidation() {
		Integer id = 1;
		ClienteDto dto = new ClienteDto();
		dto.setId(id);
		dto.setNome("Updated Name");
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha("newPassword");

		// Assegura que o cliente é encontrado
		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

		// Simula uma falha na validação
		Mockito.doThrow(new DataIntegrityViolationException("CPF ou Email já cadastrados"))
				.when(validators).validaCpfEEmail(dto);

		Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			service.update(id, dto);
		});

		Assertions.assertEquals("CPF ou Email já cadastrados", exception.getMessage());
	}


	@Test
	void whenTestUpdateClientFailsDueToRepositorySave() {
		Integer id = 1;
		ClienteDto dto = new ClienteDto();
		dto.setId(id);
		dto.setNome("Updated Name");
		dto.setCpf(cliente.getCpf());
		dto.setEmail(cliente.getEmail());
		dto.setSenha("newPassword");

		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
		Mockito.when(encoder.encode(dto.getSenha())).thenReturn("hashedPassword");

		// Simula uma falha ao salvar no repositório
		Mockito.when(clienteRepository.save(Mockito.any(Cliente.class)))
				.thenThrow(new DataIntegrityViolationException("Erro ao atualizar cliente"));

		Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			service.update(id, dto);
		});

		Assertions.assertEquals("Erro ao atualizar cliente", exception.getMessage());
	}

	@Test
	void testDelete() {
		Integer id = 1;
		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
		Mockito.doNothing().when(clienteRepository).deleteById(id);

		Assertions.assertDoesNotThrow(() -> service.delete(id));
	}

	@Test
	void testDeleteWithOrdersShouldThrowException() {
		Integer id = 1;
		Cliente cliente = new Cliente();
		cliente.setId(id);
		List<Chamado> chamados = new ArrayList<>();
		chamados.add(new Chamado());
		cliente.setChamados(chamados);

		Mockito.when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

		Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			service.delete(id);
		});

		Assertions.assertEquals("Cliente possui ordens de serviço e não pode ser deletado", exception.getMessage());
		Mockito.verify(clienteRepository, Mockito.never()).deleteById(id);
	}

}
