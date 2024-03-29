package com.gabriel.helpdesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gabriel.helpdesk.services.DBService;

@Configuration
@Profile(value = { "test" })
public class TestConfig {

	@Autowired
	private DBService dbService;
	
	@Bean
	public Object instanciaDB() {
		this.dbService.instanciaDB();
		return dbService;
	};
	
}
	
