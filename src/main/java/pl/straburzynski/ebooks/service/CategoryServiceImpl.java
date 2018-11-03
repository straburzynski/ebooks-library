package pl.straburzynski.ebooks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.straburzynski.ebooks.exception.CategoryFoundException;
import pl.straburzynski.ebooks.exception.CategoryNotFoundException;
import pl.straburzynski.ebooks.model.Book;
import pl.straburzynski.ebooks.model.Category;
import pl.straburzynski.ebooks.repository.BookRepository;
import pl.straburzynski.ebooks.repository.CategoryRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(BookRepository bookRepository,
                               CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found")
        );
    }

    @Override
    public Category create(Category category) {
        Category categoryDb = categoryRepository.save(category);
        log.info("Category {} ({}) created", categoryDb.getName(), categoryDb.getId());
        return categoryDb;
    }

    @Override
    public Category update(Category category, Long categoryId) {
        Category categoryDb = findById(categoryId);
        categoryDb.setName(category.getName());
        Category categorySaved = categoryRepository.save(categoryDb);
        log.info("Category id {} updated from {} to {}", categoryDb.getId(), categoryDb.getName(), categorySaved.getName());
        return categorySaved;
    }

    @Override
    public void delete(Long categoryId) {
        Category category = findById(categoryId);
        List<Book> books = bookRepository.findByCategoriesIn(Collections.singleton(category));
        if (books.size() > 0) throw new CategoryFoundException("Category is used in some books and can not be deleted");
        categoryRepository.deleteById(categoryId);
        log.info("Category {} ({}) deleted", category.getName(), category.getId());
    }

}
