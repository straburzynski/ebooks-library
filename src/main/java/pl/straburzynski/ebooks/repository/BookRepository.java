package pl.straburzynski.ebooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.straburzynski.ebooks.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
