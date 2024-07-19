package com.gabriel.helpdesk.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.repository.PessoaRepository;
import com.gabriel.helpdesk.repository.TecnicoRepository;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TecnicoServiceTest {

	@InjectMocks
	private TecnicoService service;

	@Mock
	private TecnicoRepository tecnicoRepository;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private BCryptPasswordEncoder encoder;
	@Mock
	private Validators validators;

	private Tecnico tecnico;

	@BeforeEach
	void setUp() {
		tecnico = new Tecnico(null, "Valdir Cezar", "550.482.150-95", "valdir@mail.com", "123");
		tecnico.addPerfil(Perfil.ADMIN);
	}

	@Test
	void testFindById() {
		Integer id = 1;
		Mockito.when(tecnicoRepository.findById(id)).thenReturn(Optional.of(tecnico));

		Tecnico foundTecnico = service.findById(id);

		Assertions.assertNotNull(foundTecnico);
		Assertions.assertEquals(tecnico.getId(), foundTecnico.getId());
	}

	@Test
	void testFindAll() {
		List<Tecnico> tecnicoList = new ArrayList<>();
		tecnicoList.add(tecnico);
		Mockito.when(tecnicoRepository.findAll()).thenReturn(tecnicoList);

		List<Tecnico> foundTecnicos = service.findAll();

		Assertions.assertNotNull(foundTecnicos);
		Assertions.assertFalse(foundTecnicos.isEmpty());
		Assertions.assertEquals(1, foundTecnicos.size());
	}

	@Test
	void testCreate() {
		TecnicoDto dto = new TecnicoDto();
		dto.setNome(tecnico.getNome());
		dto.setCpf(tecnico.getCpf());
		dto.setEmail(tecnico.getEmail());
		dto.setSenha(tecnico.getSenha());
		Mockito.doNothing().when(validators).validaCpfEEmail(dto);
		Mockito.when(encoder.encode(tecnico.getSenha())).thenReturn("hashedPassword");
		Mockito.when(tecnicoRepository.save(Mockito.any(Tecnico.class))).thenAnswer(invocation -> {
			Tecnico savedTecnico = invocation.getArgument(0);
			savedTecnico.setId(1); // Assigning an ID for the saved tecnico
			return savedTecnico;
		});

		Tecnico createdTecnico = service.create(dto);

		Assertions.assertNotNull(createdTecnico);
		Assertions.assertEquals(tecnico.getNome(), createdTecnico.getNome());
	}

	@Test
	void testUpdate() {
		Integer id = 1;
		TecnicoDto dto = new TecnicoDto();
		dto.setId(id);
		dto.setNome("Updated Name");
		dto.setCpf(tecnico.getCpf());
		dto.setEmail(tecnico.getEmail());
		dto.setSenha("newPassword");

		Mockito.doNothing().when(validators).validaCpfEEmail(dto);
		Mockito.when(tecnicoRepository.findById(id)).thenReturn(Optional.of(tecnico));
		Mockito.when(encoder.encode(dto.getSenha())).thenReturn("hashedPassword");
		Mockito.when(tecnicoRepository.save(Mockito.any(Tecnico.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Tecnico updatedTecnico = service.update(id, dto);
		Assertions.assertNotNull(updatedTecnico);
		Assertions.assertEquals(dto.getNome(), updatedTecnico.getNome());
	}

	@Test
	void testDelete() {
		Integer id = 1;
		Mockito.when(tecnicoRepository.findById(id)).thenReturn(Optional.of(tecnico));
		Mockito.doNothing().when(tecnicoRepository).deleteById(id);

		Assertions.assertDoesNotThrow(() -> service.delete(id));
	}
}
