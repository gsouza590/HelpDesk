package com.gabriel.helpdesk.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Chamado;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.ChamadoDto;
import com.gabriel.helpdesk.model.enums.Prioridade;
import com.gabriel.helpdesk.model.enums.Status;
import com.gabriel.helpdesk.repository.ChamadoRepository;
import com.gabriel.helpdesk.repository.PessoaRepository;

import jakarta.validation.Valid;

@Service
public class ChamadoService {

	@Autowired
	private ChamadoRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private TecnicoService tecnicoService;
	@Autowired
	private ClienteService clienteService;

	public Chamado findById(Integer id) {
		Optional<Chamado> chamado = repository.findById(id);
		return chamado.orElseThrow(() ->new ObjectNotFoundExceptions("Chamado Não Encontrado"));
	}

	public List<Chamado> findAll() {
		return repository.findAll();
	}

	public Chamado create(@Valid ChamadoDto dto) {
		return repository.save(newChamado(dto));
	}

	private Chamado newChamado(ChamadoDto dto) {
		Tecnico tec = tecnicoService.findById(dto.getTecnico());
		Cliente cli = clienteService.findById(dto.getCliente());

		Chamado chamado = new Chamado();
		if (dto.getId() != null) {
			chamado.setId(dto.getId());
		}
		
		if(dto.getStatus().equals(2)) {
			chamado.setDataFechamento(LocalDate.now());
		}
		chamado.setTecnico(tec);
		chamado.setCliente(cli);
		chamado.setPrioridade(Prioridade.toEnum(dto.getPrioridade()));
		chamado.setStatus(Status.toEnum(dto.getStatus()));
		chamado.setTitulo(dto.getTitulo());
		chamado.setObservacoes(dto.getObservacoes());
		return chamado;
	}

	public Chamado update(Integer id, ChamadoDto dto) {

		dto.setId(id);
		Chamado old = findById(id);
		Chamado upChamado= newChamado(dto);
		upChamado.setDataAbertura(old.getDataAbertura());
		return repository.save(upChamado);
	}

	public List<Chamado> findByLoggedUser(String email) {
	    Pessoa pessoa = pessoaRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundExceptions("Pessoa não encontrada"));
        
        if (pessoa instanceof Tecnico) {
            return repository.findByTecnicoId(pessoa.getId());
        } else if (pessoa instanceof Cliente) {
            return repository.findByClienteId(pessoa.getId());
        } else {
            return new ArrayList<>();
        }
	}
	public Map<String, Long> getChamadosPorStatus() {
		List<Chamado> chamados = repository.findAll();
		return chamados.stream()
				.collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()));
	}


	public Map<String, Long> getChamadosPorPrioridade() {
		List<Chamado> chamados = repository.findAll();
		return chamados.stream()
				.collect(Collectors.groupingBy(c -> c.getPrioridade().name(), Collectors.counting()));
	}

	public Map<String, Long> getChamadosPorMes() {
		List<Chamado> chamados = repository.findAll();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
		return chamados.stream()
				.collect(Collectors.groupingBy(c -> c.getDataAbertura().format(formatter), Collectors.counting()));
	}


	public Map<String, Long> getChamadosPorTecnico() {
		List<Chamado> chamados = repository.findAll();
		return chamados.stream()
				.collect(Collectors.groupingBy(c -> c.getTecnico().getNome(), Collectors.counting()));
	}

	public Map<String, Long> getChamadosPorCliente() {
		List<Chamado> chamados = repository.findAll();
		return chamados.stream()
				.collect(Collectors.groupingBy(c -> c.getCliente().getNome(), Collectors.counting()));
	}
}
