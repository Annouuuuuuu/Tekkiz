package com.brandonkamga.tekizz.catalog.domain.repository;

import com.brandonkamga.tekizz.catalog.domain.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepositoryPort {
    List<Tag> findAll();
    List<Tag> findAllActive();
    List<Tag> findByCategoryId(Long categoryId);
    List<Tag> findActiveByCategoryId(Long categoryId);
    Optional<Tag> findById(Long id);
}
