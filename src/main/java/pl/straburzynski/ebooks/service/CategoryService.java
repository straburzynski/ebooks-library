package pl.straburzynski.ebooks.service;

import pl.straburzynski.ebooks.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category create(Category category);

    Category update(Category category, Long categoryId);

    void delete(Long categoryId);

}
