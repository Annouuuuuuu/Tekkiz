package com.brandonkamga.tekizz.catalog.application.port.in;

import com.brandonkamga.tekizz.catalog.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface GetCategoriesUseCase {
    List<Category> getActiveCategories();
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
}
