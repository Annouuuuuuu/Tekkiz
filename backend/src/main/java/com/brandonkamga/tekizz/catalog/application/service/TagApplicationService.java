package com.brandonkamga.tekizz.catalog.application.service;

import com.brandonkamga.tekizz.catalog.application.port.in.GetTagsUseCase;
import com.brandonkamga.tekizz.domain.Tag;
import com.brandonkamga.tekizz.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagApplicationService implements GetTagsUseCase {

    private final TagRepository tagRepository;

    public TagApplicationService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getActiveTags() {
        return tagRepository.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                .toList();
    }

    @Override
    public List<Tag> getTagsByCategoryId(Long categoryId) {
        return tagRepository.findByCategoryId(categoryId);
    }
}
