package pl.straburzynski.ebooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.straburzynski.ebooks.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
