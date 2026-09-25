package com.sat.tracker.service;

import com.sat.tracker.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg");

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    private Path storagePath() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
        return path;
    }

    public String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException("File must have an extension");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    public boolean isAllowed(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return false;
        }
        return ALLOWED_EXTENSIONS.contains(extractExtension(originalFilename));
    }

    /**
     * Stores the uploaded file under a random-prefixed name and returns the
     * stored relative filename (not an absolute path) which is persisted on Certificate.filePath.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Certificate file is required");
        }
        String original = file.getOriginalFilename();
        if (!isAllowed(original)) {
            throw new BadRequestException("Only PDF and JPEG files are allowed");
        }
        String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String storedName = UUID.randomUUID().toString().replace("-", "") + "_" + safeName;

        Path target = storagePath().resolve(storedName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
        return storedName;
    }

    public byte[] read(String storedName) {
        Path target = storagePath().resolve(storedName);
        if (!Files.exists(target)) {
            return null;
        }
        try {
            return Files.readAllBytes(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
