package com.gabriel.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.services.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class ClienteControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private Cliente createCliente() {
        Cliente cliente = new Cliente(1, "Cliente Teste", "111.111.111-11", "cliente@teste.com", "senha123");
        cliente.addPerfil(Perfil.CLIENTE);
        return cliente;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById() throws Exception {
        Cliente cliente = createCliente();
        when(clienteService.findById(cliente.getId())).thenReturn(cliente);

        mockMvc.perform(get("/clientes/" + cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cliente.getId())))
                .andExpect(jsonPath("$.nome", is(cliente.getNome())))
                .andExpect(jsonPath("$.cpf", is(cliente.getCpf())))
                .andExpect(jsonPath("$.email", is(cliente.getEmail())))
                .andExpect(jsonPath("$.senha", is(cliente.getSenha())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll() throws Exception {
        Cliente cliente = createCliente();
        List<Cliente> list = Arrays.asList(cliente);

        when(clienteService.findAll()).thenReturn(list);

        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(cliente.getId())))
                .andExpect(jsonPath("$[0].nome", is(cliente.getNome())))
                .andExpect(jsonPath("$[0].cpf", is(cliente.getCpf())))
                .andExpect(jsonPath("$[0].email", is(cliente.getEmail())))
                .andExpect(jsonPath("$[0].senha", is(cliente.getSenha())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create() throws Exception {
        Cliente cliente = createCliente();
        ClienteDto clienteDto = new ClienteDto(cliente);

        // Adicionando explicitamente os perfis ao DTO como inteiros
        clienteDto.setPerfis(cliente.getPerfis().stream().map(Perfil::getCodigo).collect(Collectors.toSet()));

        when(clienteService.create(any(ClienteDto.class))).thenReturn(cliente);

        String clienteJson = objectMapper.writeValueAsString(clienteDto);
        System.out.println("Cliente JSON: " + clienteJson);  // Adicionado para depuração

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isCreated());
    }
}
