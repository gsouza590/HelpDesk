package com.gabriel.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.services.TecnicoService;
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

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class TecnicoControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TecnicoService tecnicoService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private Tecnico createTecnico() {
        Tecnico tecnico = new Tecnico(1, "Tecnico Teste", "111.111.111-11", "tecnico@teste.com", "senha123");
        tecnico.addPerfil(Perfil.TECNICO);
        return tecnico;
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    @DisplayName("FindById Tecnico With Success")
    void testFindByIdTecnicoWithSuccess() throws Exception {
        Tecnico tecnico = createTecnico();
        when(tecnicoService.findById(tecnico.getId())).thenReturn(tecnico);

        mockMvc.perform(get("/tecnicos/" + tecnico.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tecnico.getId())))
                .andExpect(jsonPath("$.nome", is(tecnico.getNome())))
                .andExpect(jsonPath("$.cpf", is(tecnico.getCpf())))
                .andExpect(jsonPath("$.email", is(tecnico.getEmail())))
                .andExpect(jsonPath("$.senha", is(tecnico.getSenha())))
                .andExpect(jsonPath("$.perfis", hasItems("TECNICO")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("FindAll Tecnico With Success")
    void testFindAllTecnicoWithSuccess() throws Exception {
        Tecnico tecnico = createTecnico();
        List<Tecnico> list = Arrays.asList(tecnico);

        when(tecnicoService.findAll()).thenReturn(list);

        mockMvc.perform(get("/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(tecnico.getId())))
                .andExpect(jsonPath("$[0].nome", is(tecnico.getNome())))
                .andExpect(jsonPath("$[0].cpf", is(tecnico.getCpf())))
                .andExpect(jsonPath("$[0].email", is(tecnico.getEmail())))
                .andExpect(jsonPath("$[0].senha", is(tecnico.getSenha())))
                .andExpect(jsonPath("$[0].perfis", hasItems("TECNICO")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create Tecnico With Success")
    void testCreateTecnicoWithSuccess() throws Exception {

         String tecnicoJson = "{ \"nome\": \"Teste1\", \"cpf\": \"40683556819\", \"email\": \"teste@gmail.com\", \"senha\": \"123\", \"perfis\": [0] }";

        Tecnico tecnico = new Tecnico(1, "Teste1", "40683556819", "teste@gmail.com", "123");
        tecnico.addPerfil(Perfil.TECNICO);

        when(tecnicoService.create(any(TecnicoDto.class))).thenReturn(tecnico);

        mockMvc.perform(post("/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tecnicoJson))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String location = result.getResponse().getHeader("Location");
                    assert location != null;
                    assert location.contains("/tecnicos/1");
                });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Updated Tecnico With Success")
    void testUpdateTecnicoWithSuccess() throws Exception {
        String tecnicoJson = "{ \"nome\": \"Tecnico Atualizado\", \"cpf\": \"40683556819\", \"email\": \"atualizado@gmail.com\", \"senha\": \"nova123\", \"perfis\": [0] }";

        Tecnico tecnico = new Tecnico(1, "Tecnico Atualizado", "40683556819", "atualizado@gmail.com", "nova123");
        tecnico.addPerfil(Perfil.TECNICO);

        when(tecnicoService.update(any(Integer.class), any(TecnicoDto.class))).thenReturn(tecnico);

        mockMvc.perform(put("/tecnicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tecnicoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tecnico.getId())))
                .andExpect(jsonPath("$.nome", is(tecnico.getNome())))
                .andExpect(jsonPath("$.cpf", is(tecnico.getCpf())))
                .andExpect(jsonPath("$.email", is(tecnico.getEmail())))
                .andExpect(jsonPath("$.senha", is(tecnico.getSenha())))
                .andExpect(jsonPath("$.perfis", hasItems("TECNICO")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Tecnico With Success")
    void testDeleteTecnicoWithSucess() throws Exception {
        doNothing().when(tecnicoService).delete(any(Integer.class));

        mockMvc.perform(delete("/tecnicos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
