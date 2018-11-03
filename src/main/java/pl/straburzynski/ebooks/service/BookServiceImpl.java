package pl.straburzynski.ebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.straburzynski.ebooks.exception.BookNotFoundException;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.repository.BookRepository;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException("Book not found")
        );
    }

    @Override
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book, Long bookId) {
        Book bookDb = findById(bookId);
        bookDb.setCategories(book.getCategories());
        bookDb.setAuthors(book.getAuthors());
        bookDb.setDescription(book.getDescription());
        bookDb.setTitle(book.getTitle());
        bookDb.setFormats(book.getFormats());
        bookDb.setYear(book.getYear());
        return bookRepository.save(bookDb);
    }

    @Override
    public void delete(Long bookId) {
        bookRepository.deleteById(bookId);
    }
}
