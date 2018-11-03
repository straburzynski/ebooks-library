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
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(
                () -> new AuthorNotFoundException("Author not found")
        );
    }

    @Override
    public Author create(Author author) {
        Author authorSaved = authorRepository.save(author);
        log.info("Author created: {} ({})", authorSaved.getName(), authorSaved.getId());
        return authorSaved;
    }

    @Override
    public Author update(Author author, Long authorId) {
        Author authorDb = findById(authorId);
        authorDb.setName(author.getName());
        Author authorSaved = authorRepository.save(authorDb);
        log.info("Author updated: {}", authorSaved.toString());
        return authorSaved;
    }

    @Override
    public void delete(Long authorId) {
        authorRepository.deleteById(authorId);
        log.info("Author with id {} deleted", authorId);
    }

}
