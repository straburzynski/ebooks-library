package pl.straburzynski.ebooks.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.straburzynski.ebooks.service.BookService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/files")
@Slf4j
public class FileController {

    private final BookService bookService;

    public FileController(BookService bookService) {
        this.bookService = bookService;
    }

    @DeleteMapping("{fileId}")
    public ResponseEntity<?> deleteEbookFile(@PathVariable Long fileId) throws IOException {
        bookService.deleteEbookFile(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{fileId}")
    public ResponseEntity<Resource> getEbookFile(@PathVariable Long fileId, HttpServletRequest request) {
        Resource resource = bookService.downloadEbookFile(fileId);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
