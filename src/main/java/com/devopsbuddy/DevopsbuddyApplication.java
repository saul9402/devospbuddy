package com.devopsbuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//con esta linea le dices a Spring donde buscar los repositorios, asi evitas que busque en toda la aplicacion y mejora el rendimiento, :3
@EnableJpaRepositories(basePackages="com.devopsbuddy.backend.persistence.repositories")
public class DevopsbuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsbuddyApplication.class, args);
	}
}
