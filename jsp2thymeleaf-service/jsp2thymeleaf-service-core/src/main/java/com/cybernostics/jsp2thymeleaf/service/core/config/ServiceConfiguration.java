package com.cybernostics.jsp2thymeleaf.service.core.config;

import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

import java.nio.file.Path;

@Configuration
@ComponentScan("com.cybernostics.jsp2thymeleaf.service.core")
public class ServiceConfiguration {

    @Bean
    public JSP2ThymeleafConfiguration jsp2ThymeleafConfiguration() {
        return JSP2ThymeleafConfiguration.getBuilder()
            .withIncludes("**/*.jsp", "**/*.jspf")
            .withExcludes("")
            .withDestFolder("output")
            .withSrcFolder("input")
            .build();
    }
    
    @Bean
    public Path outputBasePath() {
        return Path.of("output");
    }
}
