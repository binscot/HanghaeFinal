package com.example.hanghaefinal;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class HanghaeFinal {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "./application.yml";

    public static void main(String[] args) {
        //SpringApplication.run(HanghaeFinal.class, args);
        new SpringApplicationBuilder(HanghaeFinal.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
