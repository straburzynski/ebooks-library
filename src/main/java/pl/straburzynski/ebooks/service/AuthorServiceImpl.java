package pl.straburzynski.ebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.straburzynski.ebooks.exception.AuthorNotFoundException;
import pl.straburzynski.ebooks.model.Author;
import pl.straburzynski.ebooks.repository.AuthorRepository;

import java.util.List;

@Service
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
        return authorRepository.save(author);
    }

    @Override
    public Author update(Author author, Long authorId) {
        Author authorDb = findById(authorId);
        authorDb.setName(author.getName());
        return authorRepository.save(authorDb);
    }

    @Override
    public void delete(Long authorId) {
        authorRepository.deleteById(authorId);
    }

}
