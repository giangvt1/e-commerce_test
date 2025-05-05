package com.sasucare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service to handle file uploads and retrievals
 */
@Service
public class FileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    private final Path fileStorageLocation;
    
    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            logger.error("Could not create the directory where uploaded files will be stored.", ex);
            throw new RuntimeException("Could not create the directory where uploaded files will be stored.", ex);
        }
    }
    
    /**
     * Store a file and return the filename
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Create a unique filename
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(dotIndex);
        }
        
        String uniqueFileName = UUID.randomUUID() + fileExtension;
        
        try {
            // Check if the filename contains invalid characters
            if (uniqueFileName.contains("..")) {
                throw new RuntimeException("Invalid filename: " + uniqueFileName);
            }
            
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return uniqueFileName;
        } catch (IOException ex) {
            logger.error("Failed to store file {}", uniqueFileName, ex);
            throw new RuntimeException("Failed to store file " + uniqueFileName, ex);
        }
    }
    
    /**
     * Load a file as a Resource
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                logger.error("File not found: {}", fileName);
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            logger.error("File not found: {}", fileName, ex);
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }
    
    /**
     * Get file URL for accessing through web
     */
    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        return "/uploads/" + fileName;
    }
}
