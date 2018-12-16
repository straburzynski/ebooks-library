package pl.straburzynski.ebooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.File;
import pl.straburzynski.ebooks.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
    }

    @GetMapping("{id}/files")
    public ResponseEntity<List<File>> getBookFiles(@PathVariable Long id) {
        return ResponseEntity.ok().body(bookService.getEbookFiles(id));
    }

    @PostMapping()
    public ResponseEntity<Book> createBookWithFiles(
            @RequestParam("book") String book,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return new ResponseEntity<>(bookService.create(book, image, files), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable Long id) {
        return new ResponseEntity<>(bookService.update(book, id), HttpStatus.CREATED);
    }

}
