package com.cybernostics.jsp2thymeleaf.sampleapp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@SpringBootApplication
public class SampleJspApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SampleJspApplication.class);
    }

    public static void main(String[] args) throws Exception {
        // Debug file paths before starting the application
        debugFilePaths();
        
        SpringApplication.run(SampleJspApplication.class, args);
    }
    
    /**
     * Debug method to check if JSP files exist in the expected locations
     */
    private static void debugFilePaths() {
        try {
            // Check working directory
            String workingDir = System.getProperty("user.dir");
            System.out.println("Working directory: " + workingDir);
            
            // Check src/main/webapp/WEB-INF/jsp path
            Path jspPath = Paths.get(workingDir, "src", "main", "webapp", "WEB-INF", "jsp");
            boolean jspExists = Files.exists(jspPath);
            System.out.println("JSP path exists: " + jspExists + " - " + jspPath);
            
            // List JSP files if directory exists
            if (jspExists) {
                System.out.println("JSP files in directory:");
                Files.list(jspPath).forEach(p -> System.out.println("  - " + p.getFileName()));
            }
            
            // Check src/main/resources/jsp path
            Path resourceJspPath = Paths.get(workingDir, "src", "main", "resources", "jsp");
            boolean resourceJspExists = Files.exists(resourceJspPath);
            System.out.println("Resource JSP path exists: " + resourceJspExists + " - " + resourceJspPath);
            
            // List JSP files in resources if directory exists
            if (resourceJspExists) {
                System.out.println("JSP files in resources directory:");
                Files.list(resourceJspPath).forEach(p -> System.out.println("  - " + p.getFileName()));
            }
        } catch (Exception e) {
            System.err.println("Error checking file paths: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    @Qualifier("addResourceHandlersFunction")
    public Consumer<ResourceHandlerRegistry> resourceHandlersFunction() {
        System.out.println("registered jsp bean");
        return (r) -> {
            r.addResourceHandler("/jsp*/**").addResourceLocations("classpath:/");
            
            // Add specific handler for WEB-INF/jsp if it exists
            File webInfJsp = new File(System.getProperty("user.dir"), "src/main/webapp/WEB-INF/jsp");
            if (webInfJsp.exists()) {
                r.addResourceHandler("/webinf-jsp/**").addResourceLocations("file:" + webInfJsp.getAbsolutePath() + "/");
                System.out.println("Added resource handler for: " + webInfJsp.getAbsolutePath());
            }
        };
    }
}
