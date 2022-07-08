package com.gen.GeneralModule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.gen.GeneralModule.entities"})
public class GeneralModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneralModuleApplication.class, args);

	}

}
