package pl.straburzynski.ebooks.service;

import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.model.Book;

import java.util.List;

public interface BookService {

    List<Book> findAll();

    Book findById(Long id);

    Book create(String book, MultipartFile image, MultipartFile[] files);

    Book update(Book book, Long bookId);

    void delete(Long bookId);

}
