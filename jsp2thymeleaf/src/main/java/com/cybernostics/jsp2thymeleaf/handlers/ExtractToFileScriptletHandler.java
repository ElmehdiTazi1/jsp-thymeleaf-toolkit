package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import org.jdom2.Comment;
import org.jdom2.Content;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handler qui extrait les scriptlets dans des fichiers séparés pour traitement manuel ultérieur.
 * Cette approche est utile pour les migrations complexes nécessitant une refactorisation
 * manuelle du code scriptlet.
 * 
 * @author elmehdi.tazi
 */
public class ExtractToFileScriptletHandler extends BaseScriptletHandler {
    
    private final Path extractionFolder;
    
    /**
     * Constructeur avec spécification du dossier d'extraction
     * 
     * @param extractionFolder Dossier où extraire les fichiers de scriptlets
     */
    public ExtractToFileScriptletHandler(Path extractionFolder) {
        this.extractionFolder = extractionFolder;
        ensureExtractionFolderExists();
    }
    
    /**
     * Constructeur par défaut qui utilise le dossier "extracted_scriptlets" dans le répertoire temporaire
     */
    public ExtractToFileScriptletHandler() {
        this(Paths.get(System.getProperty("java.io.tmpdir"), "extracted_scriptlets"));
    }

    @Override
    public Content handleScriptlet(ScriptletContext ctx, String scriptletCode, String filePath) {
        if (scriptletCode == null || scriptletCode.isEmpty()) {
            scriptletCode = extractScriptletCode(ctx);
        }
        
        String sanitizedCode = sanitizeScriptletCode(scriptletCode);
        
        try {
            String extractedFilePath = extractScriptletToFile(sanitizedCode, filePath);
            String commentText = " Scriptlet extrait dans le fichier: " + extractedFilePath + " ";
            logger.info("Scriptlet extrait dans: " + extractedFilePath);
            return new Comment(commentText);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction du scriptlet", e);
            return new Comment(" Erreur lors de l'extraction du scriptlet: " + e.getMessage() + " ");
        }
    }
    
    /**
     * Extrait le code du scriptlet dans un fichier
     * 
     * @param scriptletCode Code du scriptlet
     * @param originalFilePath Chemin du fichier JSP original
     * @return Chemin du fichier où le scriptlet a été extrait
     * @throws IOException En cas d'erreur d'écriture
     */
    private String extractScriptletToFile(String scriptletCode, String originalFilePath) throws IOException {
        ensureExtractionFolderExists();
        
        // Génère un nom de fichier basé sur le fichier original et un UUID
        String baseFileName = new File(originalFilePath).getName().replaceAll("\\.[^\\.]+$", "");
        String extractedFileName = baseFileName + "_scriptlet_" + UUID.randomUUID().toString().substring(0, 8) + ".java";
        Path extractedFilePath = extractionFolder.resolve(extractedFileName);
        
        // Écrit le contenu du scriptlet dans le fichier
        try (FileWriter writer = new FileWriter(extractedFilePath.toFile())) {
            writer.write("// Scriptlet extrait du fichier: " + originalFilePath + "\n");
            writer.write("// Date d'extraction: " + java.time.LocalDateTime.now() + "\n");
            writer.write("// Ce fichier est destiné à être converti en code Thymeleaf ou en service Spring\n\n");
            writer.write(scriptletCode);
        }
        
        return extractedFilePath.toString();
    }
    
    /**
     * S'assure que le dossier d'extraction existe et le crée si nécessaire
     */
    private void ensureExtractionFolderExists() {
        if (!Files.exists(extractionFolder)) {
            try {
                Files.createDirectories(extractionFolder);
                logger.info("Dossier d'extraction créé: " + extractionFolder);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Impossible de créer le dossier d'extraction: " + extractionFolder, e);
            }
        }
    }
}