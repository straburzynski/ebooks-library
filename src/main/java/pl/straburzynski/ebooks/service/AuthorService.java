package pl.straburzynski.ebooks.service;

import pl.straburzynski.ebooks.model.Author;

import java.util.List;

public interface AuthorService {

    List<Author> findAll();

    Author findById(Long id);

    Author create(Author author);

    Author update(Author author, Long authorId);

    void delete(Long authorId);

}
