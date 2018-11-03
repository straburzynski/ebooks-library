package pl.straburzynski.ebooks.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        book.setAuthors(handleAuthors(book.getAuthors()));
        book.setCategories(handleCategories(book.getCategories()));
        Book bookSaved = bookRepository.save(book);
        log.info("Book created: {}", bookSaved.toString());
        return bookSaved;
    }

    @Override
    public Book update(Book book, Long bookId) {
        Book bookDb = findById(bookId);
        bookDb.setCategories(handleCategories(book.getCategories()));
        bookDb.setAuthors(handleAuthors(book.getAuthors()));
        bookDb.setDescription(book.getDescription());
        bookDb.setTitle(book.getTitle());
        bookDb.setFormats(book.getFormats());
        bookDb.setYear(book.getYear());
        Book bookSaved = bookRepository.save(bookDb);
        log.info("Book updated: {}", bookSaved.toString());
        return bookSaved;
    }

    @Override
    public void delete(Long bookId) {
        bookRepository.deleteById(bookId);
        log.info("Book with id {} deleted", bookId);
    }

    private List<Author> handleAuthors(List<Author> authors) {
        return authors.stream().map(author -> {
                    if (author.getId() != null) {
                        return authorService.findById(author.getId());
                    } else {
                        return authorService.create(Author.builder().name(author.getName()).build());
                    }
                }
        ).collect(Collectors.toList());
    }

    private List<Category> handleCategories(List<Category> categories) {
        return categories.stream().map(category ->
                categoryService.findById(category.getId())).collect(Collectors.toList());
    }
}
