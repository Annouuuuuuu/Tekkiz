package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Provider operations.
 * Follows Interface Segregation Principle.
 */
public interface ProviderService {

    /**
     * Find a provider by its ID.
     *
     * @param id the provider ID
     * @return the provider if found
     */
    Optional<Provider> findById(Long id);

    /**
     * Find a provider by its name.
     *
     * @param providerName the provider name
     * @return the provider if found
     */
    Optional<Provider> findByProviderName(ProviderType providerName);

    /**
     * Find all providers.
     *
     * @return list of all providers
     */
    List<Provider> findAll();

    /**
     * Save a provider.
     *
     * @param provider the provider to save
     * @return the saved provider
     */
    Provider save(Provider provider);

    /**
     * Delete a provider by its ID.
     *
     * @param id the provider ID
     */
    void deleteById(Long id);

    /**
     * Check if a provider exists by name.
     *
     * @param providerName the provider name
     * @return true if exists, false otherwise
     */
    boolean existsByProviderName(ProviderType providerName);

    /**
     * Check if a provider exists by ID.
     *
     * @param id the provider ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}