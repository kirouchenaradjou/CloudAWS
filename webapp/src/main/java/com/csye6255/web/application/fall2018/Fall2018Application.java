package com.csye6255.web.application.fall2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan({"com.csye6255.web.application.fall2018.controller", "com.csye6255.web.application.fall2018.dao", "com.csye6255.web.application.fall2018service", "neu.csye6225.entity",
        "com.csye6255.web.application.fall2018.utilities"})
@SpringBootApplication
@Profile({"aws","dev"})
public class Fall2018Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources( Fall2018Application.class );
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run( Fall2018Application.class, args );
    }

}