package pl.straburzynski.ebooks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.config.ApplicationConfiguration;
import pl.straburzynski.ebooks.exception.FileNotFoundException;
import pl.straburzynski.ebooks.exception.FileStorageException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {

    private Path fileStorageLocation;
    private String uploadDir;

    @Autowired
    public FileStorageService(ApplicationConfiguration applicationConfiguration) {
        this.uploadDir = applicationConfiguration.getUploadDir();
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the uploads directory", ex);
        }
    }

    public String saveFile(MultipartFile file, String filepath) {
        try {
            validateFileName(filepath);
            Path targetLocation = Paths.get(fileStorageLocation + filepath);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", targetLocation.toString());
            return targetLocation.getFileName().toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not save file " + filepath, ex);
        }
    }

    public boolean deleteFile(String filePath) throws IOException {
        Path file = Paths.get(fileStorageLocation + filePath);
        boolean isDeleted = Files.deleteIfExists(file);
        if (isDeleted) {
            log.info("File deleted: {}", filePath);
        } else {
            log.info("File could not be deleted: {}", filePath);
        }
        return isDeleted;
    }

    public Resource loadFileAsResource(String filePath) {
        try {
            Resource resource = new UrlResource(Paths.get(fileStorageLocation + filePath).toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("Wrong path exception " + filePath, ex);
        }
    }

    public byte[] loadFileAsByteArray(String filepath) throws IOException {
        File file = new File(fileStorageLocation + filepath);
        if (file.exists() && !file.isDirectory()) {
            return Files.readAllBytes(Paths.get(fileStorageLocation + filepath));
        } else {
            String noBookImagePath = uploadDir + "/no-image.jpg";
            return Files.readAllBytes(Paths.get(noBookImagePath));
        }
    }

    private void validateFileName(String filepath) {
        if (filepath.contains("..")) {
            throw new FileStorageException("Filepath contains invalid sequence " + filepath);
        }
    }

    public void createSubFolder(String folderName) {
        try {
            Path subFolder = Paths.get(uploadDir + "/" + folderName).toAbsolutePath().normalize();
            if (!Files.isDirectory(subFolder)) {
                Files.createDirectories(subFolder);
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not create subfolder: " + folderName, ex);
        }
    }

}
