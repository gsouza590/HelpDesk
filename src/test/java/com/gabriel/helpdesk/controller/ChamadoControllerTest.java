package com.gabriel.helpdesk.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.helpdesk.model.Chamado;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.ChamadoDto;
import com.gabriel.helpdesk.model.enums.Prioridade;
import com.gabriel.helpdesk.model.enums.Status;
import com.gabriel.helpdesk.services.ChamadoService;

@SpringBootTest
@ActiveProfiles("test")
class ChamadoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ChamadoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private Cliente createCliente() {
        return new Cliente(1, "Cliente Teste", "111.111.111-11", "cliente@teste.com", "senha123");
    }

    private Tecnico createTecnico() {
        return new Tecnico(1, "Tecnico Teste", "222.222.222-22", "tecnico@teste.com", "senha123");
    }

    private Chamado createChamado(Cliente cliente, Tecnico tecnico) {
        Chamado chamado = new Chamado();
        chamado.setId(1);
        chamado.setTitulo("Chamado de Teste");
        chamado.setObservacoes("Descrição do chamado de teste");
        chamado.setPrioridade(Prioridade.MEDIA);
        chamado.setStatus(Status.ABERTO);
        chamado.setCliente(cliente);
        chamado.setTecnico(tecnico);
        return chamado;
    }

    private ChamadoDto createChamadoDto(Chamado chamado) {
        ChamadoDto chamadoDto = new ChamadoDto();
        chamadoDto.setTitulo(chamado.getTitulo());
        chamadoDto.setObservacoes(chamado.getObservacoes());
        chamadoDto.setPrioridade(chamado.getPrioridade().getCodigo());
        chamadoDto.setStatus(chamado.getStatus().getCodigo());
        chamadoDto.setCliente(chamado.getCliente().getId());
        chamadoDto.setTecnico(chamado.getTecnico().getId());
        return chamadoDto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindAll Chamado With Success")
    void testFindAllChamadoWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        Tecnico tecnico = createTecnico();
        Chamado chamado = createChamado(cliente, tecnico);

        when(service.findAll()).thenReturn(List.of(chamado));

        mockMvc.perform(get("/chamados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(chamado.getId())))
                .andExpect(jsonPath("$[0].titulo", is(chamado.getTitulo())))
                .andExpect(jsonPath("$[0].observacoes", is(chamado.getObservacoes())))
                .andExpect(jsonPath("$[0].tecnico", is(tecnico.getId())))
                .andExpect(jsonPath("$[0].cliente", is(cliente.getId())))
                .andExpect(jsonPath("$[0].nomeTecnico", is(tecnico.getNome())))
                .andExpect(jsonPath("$[0].nomeCliente", is(cliente.getNome())))
        ;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindById Chamado With Success")
    void testFindByIdChamadoWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        Tecnico tecnico = createTecnico();
        Chamado chamado = createChamado(cliente, tecnico);

        when(service.findById(chamado.getId())).thenReturn(chamado);

        mockMvc.perform(get("/chamados/" + chamado.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(chamado.getId())))
                .andExpect(jsonPath("$.titulo", is(chamado.getTitulo())))
                .andExpect(jsonPath("$.observacoes", is(chamado.getObservacoes())))
                .andExpect(jsonPath("$.tecnico", is(tecnico.getId())))
                .andExpect(jsonPath("$.cliente", is(cliente.getId())))
                .andExpect(jsonPath("$.nomeTecnico", is(tecnico.getNome())))
                .andExpect(jsonPath("$.nomeCliente", is(cliente.getNome())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindById Chamado Not Found")
    void testFindByIdChamadoNotFound() throws Exception {
        Integer chamadoId = 3;

        when(service.findById(chamadoId)).thenThrow(new ObjectNotFoundExceptions("Chamado não Encontrado"));

        mockMvc.perform(get("/chamados/" + chamadoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error", is("Object Not Found")))
                .andExpect(jsonPath("$.message", is("Chamado não Encontrado")))
                .andExpect(jsonPath("$.path", is("/chamados/" + chamadoId)));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Chamado With Success")
    void testCreateChamadoWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        Tecnico tecnico = createTecnico();
        Chamado chamado = createChamado(cliente, tecnico);
        ChamadoDto chamadoDto = createChamadoDto(chamado);

        when(service.create(any(ChamadoDto.class))).thenReturn(chamado);

        mockMvc.perform(post("/chamados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chamadoDto)))
                .andExpect(status().isCreated()).andReturn();


    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Updated Chamado With Success")
    void testUpdateChamadoWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        Tecnico tecnico = createTecnico();
        Chamado chamado = createChamado(cliente, tecnico);
        ChamadoDto chamadoDto = createChamadoDto(chamado);

        when(service.update(anyInt(),any(ChamadoDto.class))).thenReturn(chamado);

        mockMvc.perform(put("/chamados/" + chamado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chamadoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(chamado.getId())))
                .andExpect(jsonPath("$.titulo", is(chamado.getTitulo())))
                .andExpect(jsonPath("$.observacoes", is(chamado.getObservacoes())));

    }
    @Test
    @WithMockUser(username = "cliente@teste.com", roles = "CLIENTE")
    @DisplayName("FindByLoggedUser Chamado With Success")
    void testFindByLoggedUserChamadoWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        Tecnico tecnico = createTecnico();
        Chamado chamado = createChamado(cliente, tecnico);

        when(service.findByLoggedUser(anyString())).thenReturn(List.of(chamado));

        mockMvc.perform(get("/chamados/meus-chamados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(chamado.getId())))
                .andExpect(jsonPath("$[0].titulo", is(chamado.getTitulo())))
                .andExpect(jsonPath("$[0].observacoes", is(chamado.getObservacoes())));
    }

}
