package com.csye6255.web.application.fall2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class Fall2018Application {

    public static void main(String[] args) {
        SpringApplication.run(Fall2018Application.class, args);
    }
}
