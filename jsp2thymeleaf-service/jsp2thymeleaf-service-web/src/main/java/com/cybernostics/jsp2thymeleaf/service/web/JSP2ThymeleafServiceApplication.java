package com.cybernostics.jsp2thymeleaf.service.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.cybernostics.jsp2thymeleaf.service.core",
    "com.cybernostics.jsp2thymeleaf.service.web"
})
@OpenAPIDefinition(
    info = @Info(
        title = "JSP to Thymeleaf Conversion Service API",
        version = "1.0",
        description = "REST API for converting JSP files to Thymeleaf templates"
    )
)
public class JSP2ThymeleafServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JSP2ThymeleafServiceApplication.class, args);
    }
}
