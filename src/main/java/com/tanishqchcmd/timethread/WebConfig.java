package com.tanishqchcmd.timethread;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This creates a safe folder named "uploads" in your main project directory
        Path uploadDir = Paths.get(System.getProperty("user.dir") + "/uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Tells Spring: "If a URL starts with /uploads/, look in this specific folder"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}