package pl.straburzynski.ebooks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class EbooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbooksApplication.class, args);
		log.info("Application started");
	}
}
