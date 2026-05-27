package com.brandonkamga.tekizz.catalog.application.port.in;

import com.brandonkamga.tekizz.domain.Category;
import java.util.List;

public interface GetCategoriesUseCase {
    List<Category> getActiveCategories();
    List<Category> getAllCategories();
}
