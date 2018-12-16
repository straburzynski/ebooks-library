package pl.straburzynski.ebooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findById(Long id);

    List<Book> findByCategoriesIn(Set<Category> category);

}
