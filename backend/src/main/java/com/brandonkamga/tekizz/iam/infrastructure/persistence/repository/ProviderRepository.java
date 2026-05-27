package com.brandonkamga.tekizz.iam.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brandonkamga.tekizz.iam.domain.model.vo.ProviderType;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByProviderName(ProviderType providerName);

    boolean existsByProviderName(ProviderType providerName);
}
