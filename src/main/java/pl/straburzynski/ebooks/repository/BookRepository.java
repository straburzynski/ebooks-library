package pl.straburzynski.ebooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.Category;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategoriesIn(Set<Category> category);

}
