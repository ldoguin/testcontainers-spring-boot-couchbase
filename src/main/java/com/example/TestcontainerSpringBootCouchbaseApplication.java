package com.example;

import com.couchbase.client.java.Bucket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestcontainerSpringBootCouchbaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestcontainerSpringBootCouchbaseApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(final Bucket bucket) throws Exception {
		return args -> {
			bucket.get("doc");
		};
	}

}

