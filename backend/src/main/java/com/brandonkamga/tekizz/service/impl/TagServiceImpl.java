package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Tag;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.TagRepository;
import com.brandonkamga.tekizz.service.interfaces.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of TagService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAllActive() {
        return tagRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByNameIn(List<String> names) {
        return tagRepository.findByNameIn(names);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findBySlugIn(List<String> slugs) {
        return tagRepository.findBySlugIn(slugs);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteById(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag", "id", id);
        }
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return tagRepository.existsBySlug(slug);
    }

    @Override
    public Tag activate(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tag.setIsActive(true);
        return tagRepository.save(tag);
    }

    @Override
    public Tag deactivate(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tag.setIsActive(false);
        return tagRepository.save(tag);
    }
}
