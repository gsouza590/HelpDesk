package com.gabriel.helpdesk.integrationtests.swagger;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.gabriel.helpdesk.configs.TestConfigs;
import com.gabriel.helpdesk.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	public void shoulDisplaySwaggerUiPage() {
		String content = given().basePath("/swagger-ui/index.html").port(TestConfigs.SERVER_PORT).when().get().then()
				.statusCode(200).extract().body().asString();

		assertTrue(content.contains("Swagger UI"));
	}
}
