package pl.straburzynski.ebooks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.straburzynski.ebooks.exception.AuthorNotFoundException;
import pl.straburzynski.ebooks.model.Author;
import pl.straburzynski.ebooks.repository.AuthorRepository;

import java.util.List;

@Service
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public List<Author> findByName(String name) {
        return authorRepository.findByNameContains(name);
    }

    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(
                () -> new AuthorNotFoundException("Author not found")
        );
    }

    public Author create(Author author) {
        Author authorSaved = authorRepository.save(author);
        log.info("Author created: {} ({})", authorSaved.getName(), authorSaved.getId());
        return authorSaved;
    }

    public Author update(Author author, Long authorId) {
        Author authorDb = findById(authorId);
        authorDb.setName(author.getName());
        Author authorSaved = authorRepository.save(authorDb);
        log.info("Author updated: {}", authorSaved.toString());
        return authorSaved;
    }

    public void delete(Long authorId) {
        authorRepository.deleteById(authorId);
        log.info("Author with id {} deleted", authorId);
    }

}
