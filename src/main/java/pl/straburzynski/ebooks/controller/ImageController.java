package pl.straburzynski.ebooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.service.BookService;
import pl.straburzynski.ebooks.service.FileStorageService;

import java.io.IOException;

@RestController
public class ImageController {

    private final FileStorageService fileStorageService;
    private final BookService bookService;

    @Autowired
    public ImageController(FileStorageService fileStorageService, BookService bookService) {
        this.fileStorageService = fileStorageService;
        this.bookService = bookService;
    }

    @GetMapping(value = "/image/{bookId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBookImage(@PathVariable Long bookId) throws IOException {
        Book book = bookService.findById(bookId);
        return new ResponseEntity<>(fileStorageService.loadBookImageAsByteArray(book), HttpStatus.OK);
    }

}
