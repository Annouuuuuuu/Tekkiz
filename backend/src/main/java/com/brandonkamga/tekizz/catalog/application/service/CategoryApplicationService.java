package com.brandonkamga.tekizz.catalog.application.service;

import com.brandonkamga.tekizz.catalog.application.port.in.GetCategoriesUseCase;
import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryApplicationService implements GetCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
