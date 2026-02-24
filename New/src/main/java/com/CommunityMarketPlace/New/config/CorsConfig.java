package com.CommunityMarketPlace.New.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints in your API
                .allowedOrigins("https://opermart-theta.vercel.app") // Replace with your actual Vercel URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow standard HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow sending of cookies or auth headers if needed
    }
}