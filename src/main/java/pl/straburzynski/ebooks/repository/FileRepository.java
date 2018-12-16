package pl.straburzynski.ebooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.straburzynski.ebooks.model.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findFilesByBookId(Long bookId);

}
