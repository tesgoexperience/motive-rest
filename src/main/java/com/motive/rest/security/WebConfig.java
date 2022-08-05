package com.motive.rest.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${REACT_CLIENT}")
    String ReactClient;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**"); //TODO only allow any client access to specific instance controllers
        // .allowedOrigins(ReactClient);

    }

}
