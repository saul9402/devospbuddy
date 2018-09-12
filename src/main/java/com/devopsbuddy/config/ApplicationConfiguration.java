package com.devopsbuddy.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//con esta linea le dices a Spring donde buscar los repositorios, asi evitas que busque en toda la aplicacion y mejora el rendimiento, :3
@EnableJpaRepositories(basePackages="com.devopsbuddy.backend.persistence.repositories")
@EntityScan(basePackages = "com.devopsbuddy.backend.persistence.domain.backend")
@EnableTransactionManagement
@PropertySource("file:///${user.home}/devopsbuddy/application-common.properties")
public class ApplicationConfiguration {

}
