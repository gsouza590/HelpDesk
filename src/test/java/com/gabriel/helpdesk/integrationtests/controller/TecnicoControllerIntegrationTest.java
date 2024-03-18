package com.gabriel.helpdesk.integrationtests.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.gabriel.helpdesk.configs.TestConfigs;
import com.gabriel.helpdesk.integrationtests.dto.CredenciaisDto;
import com.gabriel.helpdesk.integrationtests.dto.TecnicoDto;
import com.gabriel.helpdesk.integrationtests.testcontainers.AbstractIntegrationTest;
import com.gabriel.helpdesk.model.enums.Perfil;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class TecnicoControllerIntegrationTest extends AbstractIntegrationTest {

	private static RequestSpecification especificacao;

	private static TecnicoDto tecnico;
	private static CredenciaisDto credenciais;
	private static BCryptPasswordEncoder encoder;

	@BeforeAll
	public static void setup() {
		tecnico = new TecnicoDto();
		encoder = new BCryptPasswordEncoder();
	}

	@Test
	@Order(0)
	public void autorizacao() {
		credenciais = new CredenciaisDto();
		credenciais.setEmail("admin@gmail.com");
		credenciais.setSenha(encoder.encode("123"));
		String accessToken = given().basePath("/auth/login").port(TestConfigs.SERVER_PORT).body(credenciais).when().post()
				.then().statusCode(200).extract().path("accessToken");

		especificacao = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testeCriar() throws JsonMappingException, JsonProcessingException {
		mockTecnico();

		var conteudo = given().spec(especificacao).header(TestConfigs.HEADER_PARAM_ORIGIN, "http://localhost")
				.body(tecnico).when().post().then().statusCode(200).extract().body().asString();

		ObjectMapper mapeador = new ObjectMapper();
		tecnico = mapeador.readValue(conteudo, TecnicoDto.class);

		assertNotNull(tecnico);

		assertNotNull(tecnico.getId());
		assertNotNull(tecnico.getNome());
		assertNotNull(tecnico.getCpf());
		assertNotNull(tecnico.getEmail());
		assertNotNull(tecnico.getPerfis());

		assertTrue(tecnico.getId() > 0);

		assertEquals("Valdir Cezar", tecnico.getNome());
		assertEquals("550.482.150-95", tecnico.getCpf());
		assertEquals("valdir@mail.com", tecnico.getEmail());
	}

	private void mockTecnico() {
		tecnico.setNome("Valdir Cezar");
		tecnico.setCpf("550.482.150-95");
		tecnico.setEmail("valdir@mail.com");
		tecnico.setSenha(encoder.encode("123"));
		tecnico.addPerfil(Perfil.ADMIN);
	}
}
