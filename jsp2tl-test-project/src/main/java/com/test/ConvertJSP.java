package com.test;

import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.common.ScriptletHandlingStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple utility to run JSP to Thymeleaf conversion directly using the API
 */
public class ConvertJSP {
    public static void main(String[] args) {
        try {
            System.out.println("Starting JSP to Thymeleaf conversion...");
            
            // Get current working directory
            Path currentDir = Paths.get("").toAbsolutePath();
            System.out.println("Current directory: " + currentDir);
            
            // Setup source and destination directories
            Path srcDir = currentDir.resolve("src/main/webapp/WEB-INF/jsp");
            Path destDir = currentDir.resolve("src/main/resources/templates");
            
            System.out.println("Source directory: " + srcDir);
            System.out.println("Destination directory: " + destDir);
            
            // Check if source directory exists
            if (!srcDir.toFile().exists()) {
                System.err.println("ERROR: Source directory does not exist: " + srcDir);
                System.exit(1);
            }
            
            // Create destination directory if it doesn't exist
            if (!destDir.toFile().exists()) {
                destDir.toFile().mkdirs();
                System.out.println("Created destination directory: " + destDir);
            }
            
            // Configure conversion
            JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.getBuilder()
                .withSrcFolder(srcDir.toString())
                .withDestFolder(destDir.toString())
                .withIncludes("**/*.jsp", "**/*.jspx", "**/*.jspf")
                .withScriptletStrategy(ScriptletHandlingStrategy.THYMELEAF_COMMENT)
                .withShowBanner(true)
                .build();
            
            // Run conversion
            JSP2Thymeleaf converter = new JSP2Thymeleaf(config);
            converter.run();
            
            System.out.println("Conversion completed. Thymeleaf templates should be available at: " + destDir);
            
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}