package pl.straburzynski.ebooks.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ApplicationConfiguration {

    @Value("${file.upload-dir}")
    private String uploadDir;

}