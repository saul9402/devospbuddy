package com.devopsbuddy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
//con esta linea le dices a Spring donde buscar los repositorios, asi evitas que busque en toda la aplicacion y mejora el rendimiento, :3
@EnableJpaRepositories(basePackages = "com.devopsbuddy.backend.persistence.repositories")
@EntityScan(basePackages = "com.devopsbuddy.backend.persistence.domain.backend")
//habilita @Transactional correctamente
@EnableTransactionManagement
@PropertySource("file:///${user.home}/devopsbuddy/application-common.properties")
public class ApplicationConfiguration {

	@Value("${aws.s3.profile}")
	private String awsProfileName;

	@Bean
	public AmazonS3 s3Client() {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new  ProfileCredentialsProvider(awsProfileName)).build();
		/*
		 * porque #deprecado
		 *  AWSCredentials credentials = new
		 * ProfileCredentialsProvider(awsProfileName).getCredentials(); AmazonS3Client
		 * s3Client = new AmazonS3Client(credentials); Region region =
		 * Region.getRegion(Regions.EU_WEST_1); s3Client.setRegion(region);
		 */
		return s3Client;
	}
}
