package pl.straburzynski.ebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.straburzynski.ebooks.exception.BookNotFoundException;
import pl.straburzynski.ebooks.model.Author;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.Category;
import pl.straburzynski.ebooks.repository.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final AuthorService authorService;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;

    @Autowired
    public BookServiceImpl(AuthorService authorService,
                           BookRepository bookRepository, CategoryService categoryService) {
        this.authorService = authorService;
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
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
        book.setAuthors(convertAuthors(book.getAuthors()));
        book.setCategories(convertCategories(book.getCategories()));
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book, Long bookId) {
        Book bookDb = findById(bookId);
        bookDb.setCategories(convertCategories(book.getCategories()));
        bookDb.setAuthors(convertAuthors(book.getAuthors()));
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

    private List<Author> convertAuthors(List<Author> authors) {
        return authors.stream().map(author ->
                authorService.findById(author.getId())).collect(Collectors.toList());
    }

    private List<Category> convertCategories(List<Category> categories) {
        return categories.stream().map(category ->
                categoryService.findById(category.getId())).collect(Collectors.toList());
    }
}
