package pl.straburzynski.ebooks.service;

import pl.straburzynski.ebooks.model.Book;

import java.util.List;

public interface BookService {

    List<Book> findAll();

    Book findById(Long id);

    Book create(Book book);

    Book update(Book book, Long bookId);

    void delete(Long bookId);

}
