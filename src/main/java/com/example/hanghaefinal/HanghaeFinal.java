package com.example.hanghaefinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableJpaAuditing
@EnableWebMvc
@SpringBootApplication
public class HanghaeFinal {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "classpath:aws.yml";

    public static void main(String[] args) {
        //SpringApplication.run(HanghaeFinal.class, args);
        new SpringApplicationBuilder(HanghaeFinal.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
