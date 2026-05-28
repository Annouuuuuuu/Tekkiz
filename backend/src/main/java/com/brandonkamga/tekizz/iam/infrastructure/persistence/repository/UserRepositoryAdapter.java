package com.brandonkamga.tekizz.iam.infrastructure.persistence.repository;

import com.brandonkamga.tekizz.iam.domain.model.User;
import com.brandonkamga.tekizz.iam.domain.repository.UserRepositoryPort;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.UserJpaEntity;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements the domain port using Spring Data JPA.
 */
@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpa = mapper.toJpaEntity(user);
        UserJpaEntity saved = userJpaRepository.save(jpa);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }
}
