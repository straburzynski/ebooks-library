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
        this.fileStorageLocation = Paths.get(applicationConfiguration.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
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
        String extension = StringUtils.getFilenameExtension(fileName);
        String newFileName = book.getId() + "_" + book.getTitle() + "." + extension;
        return saveFile(file, newFileName);
    }

    private String saveFile(MultipartFile file, String filename) {
        try {
            if (filename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + filename);
            }
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", filename);
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
        String filepath = this.fileStorageLocation.toString() + "/" + fileName;
        File file = new File(filepath);
        if (file.exists() && !file.isDirectory()) {
            return Files.readAllBytes(Paths.get(filepath));
        } else {
            String noRaceImagePath = uploadDir + "/no-image.jpg";
            return Files.readAllBytes(Paths.get(noRaceImagePath));
        }
    }

}
