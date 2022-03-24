package com.example.hanghaefinal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // header 확장시킴.
        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://main.d2l6bnge3hnh7g.amplifyapp.com")
                .exposedHeaders("Authorization");

    }
}

