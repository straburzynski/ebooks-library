package pl.straburzynski.ebooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.exception.BookConvertException;
import pl.straburzynski.ebooks.exception.BookNotFoundException;
import pl.straburzynski.ebooks.model.*;
import pl.straburzynski.ebooks.repository.BookRepository;
import pl.straburzynski.ebooks.validator.FileValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final AuthorService authorService;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;

    @Autowired
    public BookServiceImpl(AuthorService authorService,
                           BookRepository bookRepository,
                           CategoryService categoryService,
                           ObjectMapper objectMapper,
                           FileStorageService fileStorageService) {
        this.authorService = authorService;
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
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
    public Book create(String bookJson, MultipartFile image, MultipartFile[] multipartFiles) {
        Book book = convertToBook(bookJson);
        book.setAuthors(handleAuthors(book.getAuthors()));
        book.setCategories(handleCategories(book.getCategories()));
        book.setFormats(handleFormats(multipartFiles));
        Book savedBook = bookRepository.save(book);
        savedBook.setFiles(handleFiles(multipartFiles, savedBook));
        fileStorageService.storeImage(image, savedBook);
        Book bookDb = bookRepository.save(savedBook);
        log.info("Book created: {}", bookDb.toString());
        return bookDb;
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

    private Book convertToBook(String book) {
        try {
            return objectMapper.readValue(book, Book.class);
        } catch (Exception e) {
            throw new BookConvertException("Error converting book");
        }
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


    private Set<Format> handleFormats(MultipartFile[] files) {
        Set<Format> formats = new HashSet<>();
        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(fileName);
            if (FileValidator.isValidExtension(file)) {
                formats.add(EnumUtils.getEnumIgnoreCase(Format.class, extension));
            }
        }
        return formats;
    }

    private List<File> handleFiles(MultipartFile[] files, Book book) {
        List<File> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (FileValidator.isValidExtension(file)) {
                String name = fileStorageService.storeFile(file, book);
                fileList.add(File.builder().name(name).build());
            } else {
                log.info("Wrong e-book format: {}", file.getOriginalFilename());
            }
        }
        return fileList;
    }

}
