package pl.straburzynski.ebooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.service.BookService;

import java.io.IOException;

@RestController
public class ImageController {

    private final BookService bookService;

    @Autowired
    public ImageController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/image/{bookId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBookImage(@PathVariable Long bookId) throws IOException {
        return new ResponseEntity<>(bookService.downloadEbookImage(bookId), HttpStatus.OK);
    }

    @PutMapping(value = "/image/{bookId}")
    public ResponseEntity<?> changeBookImage(@PathVariable Long bookId,
                                             @RequestParam(value = "image") MultipartFile image) {
        Book book = bookService.findById(bookId);
        return new ResponseEntity<>(bookService.uploadEbookImage(image, book), HttpStatus.OK);
    }

}
