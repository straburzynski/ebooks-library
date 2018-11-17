package pl.straburzynski.ebooks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.config.ApplicationConfiguration;
import pl.straburzynski.ebooks.exception.FileNotFoundException;
import pl.straburzynski.ebooks.exception.FileStorageException;
import pl.straburzynski.ebooks.model.Book;

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

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        return saveFile(file, fileName);
    }

    public String storeFile(MultipartFile file, Book book) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String folderName = book.getId() + "_" + book.getTitle();
        return saveFile(file, folderName, fileName);
    }

    public void storeImage(MultipartFile file, Book book) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(fileName);
        String folderName = book.getId() + "_" + book.getTitle();
        String newFileName = folderName + "." + extension;
        saveFile(file, folderName, newFileName);
    }

    private String saveFile(MultipartFile file, String filename) {
        try {
            validateFileName(filename);
            Path targetLocation = fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", targetLocation.toString());
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filename, ex);
        }
    }

    private String saveFile(MultipartFile file, String folder, String filename) {
        try {
            validateFileName(filename);
            Path targetLocation = createSubFolder(folder).resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", targetLocation.toString());
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filename, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    public byte[] loadBookImageAsByteArray(Book book) throws IOException {
        String fileName = book.getId() + "_" + book.getTitle() + ".jpg";
        String folderName = book.getId() + "_" + book.getTitle() + "/";
        String filepath = fileStorageLocation.toString() + "/" + folderName + fileName;
        File file = new File(filepath);
        if (file.exists() && !file.isDirectory()) {
            return Files.readAllBytes(Paths.get(filepath));
        } else {
            String noBookImagePath = uploadDir + "/no-image.jpg";
            return Files.readAllBytes(Paths.get(noBookImagePath));
        }
    }

    private void validateFileName(String filename) {
        if (filename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence " + filename);
        }
    }

    private Path createSubFolder(String folderName) {
        try {
            Path subFolder = Paths.get(this.uploadDir + "/" + folderName).toAbsolutePath().normalize();
            return Files.createDirectories(subFolder);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create subfolder: " + folderName, ex);
        }
    }

}
