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

import com.gabriel.helpdesk.model.Chamado;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.ChamadoDto;
import com.gabriel.helpdesk.model.enums.Prioridade;
import com.gabriel.helpdesk.model.enums.Status;
import com.gabriel.helpdesk.repository.ChamadoRepository;
import com.gabriel.helpdesk.repository.ClienteRepository;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

	@InjectMocks
	private ChamadoService service;
	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private ChamadoRepository chamadoRepository;
	@Mock
	private TecnicoService tecnicoService;
	@Mock
	private ClienteService clienteService;

	private Cliente cliente;
	private Tecnico tecnico;
	private Chamado chamado;

	@BeforeEach
	void setUp() {
		cliente = new Cliente(null, "Albert Einstein", "111.661.890-74", "einstein@mail.com", "123");
		tecnico = new Tecnico(null, "Valdir Cezar", "550.482.150-95", "valdir@mail.com", "123");

		chamado = new Chamado(null, Prioridade.MEDIA, Status.ANDAMENTO, "Chamado 1", "Teste chamado 1", tecnico,
				cliente);
	}

	@Test
	void testFindById() {
		Integer id = 1;
		Mockito.when(chamadoRepository.findById(id)).thenReturn(Optional.of(chamado));

		Chamado foundChamado = service.findById(id);

		Assertions.assertNotNull(foundChamado);
		Assertions.assertEquals(chamado.getId(), foundChamado.getId());
	}

	@Test
	void testFindAll() {
		List<Chamado> chamadoList = new ArrayList<>();
		chamadoList.add(chamado);
		Mockito.when(chamadoRepository.findAll()).thenReturn(chamadoList);

		List<Chamado> foundChamados = service.findAll();

		Assertions.assertNotNull(foundChamados);
		Assertions.assertFalse(foundChamados.isEmpty());
		Assertions.assertEquals(1, foundChamados.size());
	}

	@Test
	void testCreate() {
		ChamadoDto dto = new ChamadoDto();
		dto.setTitulo(chamado.getTitulo());
		dto.setObservacoes(chamado.getObservacoes());
		dto.setPrioridade(2);
		dto.setStatus(1);
		dto.setTecnico(tecnico.getId());
		dto.setCliente(cliente.getId());

		Mockito.when(tecnicoService.findById(tecnico.getId())).thenReturn(tecnico);
		Mockito.when(clienteService.findById(cliente.getId())).thenReturn(cliente);
		Mockito.when(chamadoRepository.save(Mockito.any(Chamado.class))).thenAnswer(invocation -> {
			Chamado savedChamado = invocation.getArgument(0);
			savedChamado.setId(1);
			return savedChamado;
		});

		Chamado createdChamado = service.create(dto);

		Assertions.assertNotNull(createdChamado);
		Assertions.assertEquals(chamado.getTitulo(), createdChamado.getTitulo());
	}

	@Test
	void testUpdate() {
		Integer id = 1;
		ChamadoDto dto = new ChamadoDto();
		dto.setId(id);
		dto.setTitulo("Título Atualizado");
		dto.setObservacoes("Observações Atualizadas");
		dto.setPrioridade(0);
		dto.setStatus(2);
		dto.setTecnico(tecnico.getId());
		dto.setCliente(cliente.getId());

		Mockito.when(tecnicoService.findById(tecnico.getId())).thenReturn(tecnico);
		Mockito.when(clienteService.findById(cliente.getId())).thenReturn(cliente);
		Mockito.when(chamadoRepository.findById(id)).thenReturn(Optional.of(chamado));
		Mockito.when(chamadoRepository.save(Mockito.any(Chamado.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		Chamado updatedChamado = service.update(id, dto);

		Assertions.assertNotNull(updatedChamado);
		Assertions.assertEquals(dto.getTitulo(), updatedChamado.getTitulo());
	}
}
