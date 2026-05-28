package com.brandonkamga.tekizz.catalog.application.service;

import com.brandonkamga.tekizz.catalog.application.port.in.GetTagsUseCase;
import com.brandonkamga.tekizz.catalog.domain.model.Tag;
import com.brandonkamga.tekizz.catalog.domain.repository.TagRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TagApplicationService implements GetTagsUseCase {

    private final TagRepositoryPort tagRepository;

    public TagApplicationService(TagRepositoryPort tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getActiveTags() {
        return tagRepository.findAllActive();
    }

    @Override
    public List<Tag> getTagsByCategoryId(Long categoryId) {
        return tagRepository.findActiveByCategoryId(categoryId);
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }
}
