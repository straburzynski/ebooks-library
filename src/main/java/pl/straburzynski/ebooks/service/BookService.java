package pl.straburzynski.ebooks.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.File;

import java.io.IOException;
import java.util.List;

public interface BookService {

    List<Book> findAll();

    Book findById(Long id);

    Book create(String book, MultipartFile image, MultipartFile[] files);

    Book update(Book book, Long bookId);

    void delete(Long bookId);

    // files

    List<File> getEbookFiles(Long bookId);

    void deleteEbookFile(Long fileId) throws IOException;

    Resource downloadEbookFile(Long fileId);

    byte[] downloadEbookImage(Long id) throws IOException;

    String uploadEbookFile(MultipartFile file, Book book);

    String uploadEbookImage(MultipartFile image, Book book);

}