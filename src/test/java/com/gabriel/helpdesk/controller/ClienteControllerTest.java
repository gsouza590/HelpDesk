package com.gabriel.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.services.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("FindById Cliente With Success")
    void testFindByIdClienteWithSuccess() throws Exception {
        Cliente cliente = createCliente();
        when(clienteService.findById(cliente.getId())).thenReturn(cliente);

        mockMvc.perform(get("/clientes/" + cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cliente.getId())))
                .andExpect(jsonPath("$.nome", is(cliente.getNome())))
                .andExpect(jsonPath("$.cpf", is(cliente.getCpf())))
                .andExpect(jsonPath("$.email", is(cliente.getEmail())))
                .andExpect(jsonPath("$.senha", is(cliente.getSenha())))
                .andExpect(jsonPath("$.perfis", hasItems("CLIENTE")));
    }

    @Test
    @DisplayName("Access Endpoint Without Authentication")
    void testAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindAll Cliente With Success")
    void testFindAllClienteWithSuccess() throws Exception {
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
                .andExpect(jsonPath("$[0].senha", is(cliente.getSenha())))
                .andExpect(jsonPath("$[0].perfis", hasItems("CLIENTE")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Cliente With Success")
    void testCreateClienteWithSuccess() throws Exception {
        // JSON de entrada
        String clienteJson = "{ \"nome\": \"TesteCliente\", \"cpf\": \"81182698093\", \"email\": \"teste2@gmail.com\", \"senha\": \"123\", \"perfis\": [1] }";

        Cliente cliente = new Cliente();
        cliente.setId(1); // Simule o ID gerado automaticamente pelo banco de dados
        cliente.setNome("TesteCliente");
        cliente.setCpf("81182698093");
        cliente.setEmail("teste2@gmail.com");
        cliente.setSenha("123");
        cliente.addPerfil(Perfil.CLIENTE);

        when(clienteService.create(any(ClienteDto.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(result -> {
                    String location = result.getResponse().getHeader("Location");
                    assert location != null;
                    assert location.contains("/clientes/1") ;
                });
}
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Cliente With Email BadRequest")
    void testCreateClienteWithEmailBadRequest() throws Exception {

        String clienteJson = "{ \"nome\": \"TesteCliente\", \"cpf\": \"16124957027\", \"email\": \"teste2gmail.com\", \"senha\": \"123\", \"perfis\": [1] }";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/clientes"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].fieldName", is("email")))
                .andExpect(jsonPath("$.errors[0].message", is("Email inválido")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Cliente With CPF BadRequest")
    void testCreateClienteWithCPFBadRequest() throws Exception {
        // JSON de entrada com e-mail inválido
        String clienteJson = "{ \"nome\": \"TesteCliente\", \"cpf\": \"111\", \"email\": \"teste2@gmail.com\", \"senha\": \"123\", \"perfis\": [1] }";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/clientes"))
                .andExpect(jsonPath("$.errors", hasSize(1))) // Ajuste o tamanho de acordo com a quantidade de erros esperados
                .andExpect(jsonPath("$.errors[0].fieldName", is("cpf"))) // Campo específico com erro
                .andExpect(jsonPath("$.errors[0].message", is("CPF inválido"))); // Mensagem de erro esperada
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Cliente With Duplicate Data")
    void testCreateClienteWithDuplicateData() throws Exception {
        String clienteJson = "{ \"nome\": \"TesteCliente\", \"cpf\": \"81182698093\", \"email\": \"teste2@gmail.com\", \"senha\": \"123456\", \"perfis\": [1] }";

        doThrow(new DataIntegrityViolationException("Email já cadastrado")).when(clienteService).create(any(ClienteDto.class));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Violação de Dados")))
                .andExpect(jsonPath("$.message", is("Email já cadastrado")));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Updated Cliente With Success")
    void testUpdateClienteWithSuccess() throws Exception {
        String clienteJson = "{ \"nome\": \"Cliente Atualizado\", \"cpf\": \"81182698093\", \"email\": \"atualizado@gmail.com\", \"senha\": \"nova123\", \"perfis\": [1] }";

        Cliente cliente = new Cliente();
        cliente.setId(1); // Simule o ID gerado automaticamente pelo banco de dados
        cliente.setNome("Cliente Atualizado");
        cliente.setCpf("81182698093");
        cliente.setEmail("atualizado@gmail.com");
        cliente.setSenha("nova123");
        cliente.addPerfil(Perfil.CLIENTE);

        when(clienteService.update(any(Integer.class), any(ClienteDto.class))).thenReturn(cliente);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cliente.getId())))
                .andExpect(jsonPath("$.nome", is(cliente.getNome())))
                .andExpect(jsonPath("$.cpf", is(cliente.getCpf())))
                .andExpect(jsonPath("$.email", is(cliente.getEmail())))
                .andExpect(jsonPath("$.senha", is(cliente.getSenha())))
                .andExpect(jsonPath("$.perfis", hasItems("CLIENTE")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindById Cliente Not Found")
    void testFindByIdClienteNotFound() throws Exception {
        when(clienteService.findById(any(Integer.class))).thenThrow(new ObjectNotFoundExceptions("Cliente não encontrado"));


        mockMvc.perform(get("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists()) // Verifica se o timestamp existe
                .andExpect(jsonPath("$.status").value(404)) // Verifica se o status é 404
                .andExpect(jsonPath("$.error", is("Object Not Found"))) // Verifica se o error é "Object Not Found"
                .andExpect(jsonPath("$.message", is("Cliente não encontrado"))) // Verifica se a mensagem é "Cliente não encontrado"
                .andExpect(jsonPath("$.path", is("/clientes/1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Cliente With Success")
    void testDeleteClienteWithSucess() throws Exception {
        doNothing().when(clienteService).delete(any(Integer.class));

        mockMvc.perform(delete("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Cliente Not Found")
    void testDeleteClienteNotFound() throws Exception {
        doThrow(new ObjectNotFoundExceptions("Cliente não encontrado")).when(clienteService).delete(any(Integer.class));

        mockMvc.perform(delete("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Object Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));
    }

}

