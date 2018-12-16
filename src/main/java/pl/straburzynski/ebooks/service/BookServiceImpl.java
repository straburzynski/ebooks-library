package pl.straburzynski.ebooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.exception.BookConvertException;
import pl.straburzynski.ebooks.exception.BookNotFoundException;
import pl.straburzynski.ebooks.exception.FileNotFoundException;
import pl.straburzynski.ebooks.model.*;
import pl.straburzynski.ebooks.repository.BookRepository;
import pl.straburzynski.ebooks.repository.FileRepository;
import pl.straburzynski.ebooks.validator.FileValidator;

import java.io.IOException;
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
    private final FileRepository fileRepository;

    @Autowired
    public BookServiceImpl(AuthorService authorService,
                           BookRepository bookRepository,
                           CategoryService categoryService,
                           ObjectMapper objectMapper,
                           FileStorageService fileStorageService,
                           FileRepository fileRepository) {
        this.authorService = authorService;
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
        this.fileRepository = fileRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    @Override
    public Book create(String bookJson, MultipartFile image, MultipartFile[] multipartFiles) {
        Book book = convertToBook(bookJson);
        book.setAuthors(handleAuthors(book.getAuthors()));
        book.setCategories(handleCategories(book.getCategories()));
        Book savedBook = bookRepository.save(book);
        log.info("Book created: {}", savedBook.toString());
        savedBook.setFormats(handleFormats(saveFiles(multipartFiles, savedBook)));
        uploadEbookImage(image, savedBook);
        return savedBook;
    }

    @Override
    public Book update(Book book, Long bookId) {
        Book bookDb = findById(bookId);
        bookDb.setCategories(handleCategories(book.getCategories()));
        bookDb.setAuthors(handleAuthors(book.getAuthors()));
        bookDb.setDescription(book.getDescription());
        bookDb.setTitle(book.getTitle());
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

    @Override
    public List<File> getEbookFiles(Long bookId) {
        return fileRepository.findFilesByBookId(bookId);
    }

    @Override
    public Resource downloadEbookFile(Long fileId) {
        String filePath = getEbookFilePath(fileId);
        return fileStorageService.loadFileAsResource(filePath);
    }

    @Override
    public void deleteEbookFile(Long fileId) throws IOException {
        String filePath = getEbookFilePath(fileId);
        if (fileStorageService.deleteFile(filePath)) {
            fileRepository.deleteById(fileId);
        }
    }

    @Override
    public byte[] downloadEbookImage(Long id) throws IOException {
        Book book = findById(id);
        String fileName = book.getTitle() + ".jpg";
        String folderName = getFolderName(book);
        return fileStorageService.loadFileAsByteArray(folderName + fileName);
    }

    @Override
    public String uploadEbookFile(MultipartFile file, Book book) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String folderName = getFolderName(book);
        fileStorageService.createSubFolder(folderName);
        return fileStorageService.saveFile(file, folderName + filename);
    }

    @Override
    public String uploadEbookImage(MultipartFile file, Book book) {
        String folderName = getFolderName(book);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String newFileName = book.getTitle() + "." + extension;
        fileStorageService.createSubFolder(folderName);
        return fileStorageService.saveFile(file, folderName + newFileName);
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


    private Set<Format> handleFormats(Set<File> files) {
        Set<Format> formats = new HashSet<>();
        for (File file : files) {
            String extension = StringUtils.getFilenameExtension(file.getName());
            formats.add(EnumUtils.getEnumIgnoreCase(Format.class, extension));
        }
        return formats;
    }

    // todo: update formats after adding/deleting ebook file
    private void updateEbookFormats(Long bookId) {
        Set<File> files = new HashSet<>(fileRepository.findFilesByBookId(bookId));
        Book book = findById(bookId);
        book.setFormats(handleFormats(files));
        bookRepository.save(book);
    }

    private Set<File> saveFiles(MultipartFile[] files, Book book) {
        Set<File> fileList = new HashSet<>();
        for (MultipartFile file : files) {
            if (FileValidator.isValidExtension(file)) {
                String name = uploadEbookFile(file, book);
                fileList.add(File.builder()
                        .name(name)
                        .bookId(book.getId())
                        .build());
            } else {
                log.info("Wrong e-book format: {}", file.getOriginalFilename());
            }
        }
        fileRepository.saveAll(fileList);
        return fileList;
    }

    private String getFolderName(Book book) {
        return "/" + book.getId() + "_" + book.getTitle() + "/";
    }

    private String getEbookFilePath(Long fileId) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found"));
        Book book = findById(file.getBookId());
        String filename = StringUtils.cleanPath(file.getName());
        String folderName = getFolderName(book);
        return folderName + filename;
    }

}
