package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.service.interfaces.ProviderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProviderService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findById(Long id) {
        return providerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findByProviderName(ProviderType providerName) {
        return providerRepository.findByProviderName(providerName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Provider> findAll() {
        return providerRepository.findAll();
    }

    @Override
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }

    @Override
    public void deleteById(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Provider", "id", id);
        }
        providerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProviderName(ProviderType providerName) {
        return providerRepository.existsByProviderName(providerName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return providerRepository.existsById(id);
    }
}