package com.brandonkamga.tekizz.catalog.application.service;

import com.brandonkamga.tekizz.catalog.application.port.in.GetCategoriesUseCase;
import com.brandonkamga.tekizz.catalog.domain.model.Category;
import com.brandonkamga.tekizz.catalog.domain.repository.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryApplicationService implements GetCategoriesUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryApplicationService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findAllActiveOrderByDisplayOrder();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}
