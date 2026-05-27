package com.brandonkamga.tekizz.catalog.infrastructure.persistence.mapper;

import com.brandonkamga.tekizz.catalog.domain.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

    public Category toDomain(com.brandonkamga.tekizz.domain.Category entity) {
        if (entity == null) return null;
        return Category.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getDisplayOrder(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
