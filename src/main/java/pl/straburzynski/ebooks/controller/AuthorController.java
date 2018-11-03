package pl.straburzynski.ebooks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.straburzynski.ebooks.model.Author;
import pl.straburzynski.ebooks.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAuthors() {
        return new ResponseEntity<>(authorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("filter")
    public ResponseEntity<List<Author>> getAuthorsByName(@RequestParam String name) {
        return new ResponseEntity<>(authorService.findByName(name), HttpStatus.OK);
    }

}
