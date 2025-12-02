package com.proy.utp.backend_agrolink.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadDir = "uploads";

    public FileStorageService() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para guardar los archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("El archivo no tiene nombre.");
        }
        // Genera un nombre de archivo único para evitar colisiones
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia inválida.");
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Construye la URL completa para acceder al archivo
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/" + uploadDir + "/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar el archivo " + fileName, ex);
        }
    }
}