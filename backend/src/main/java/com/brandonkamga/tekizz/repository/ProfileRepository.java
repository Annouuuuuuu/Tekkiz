package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.Profile;
import com.brandonkamga.tekizz.domain.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    Optional<Profile> findByUser(User user);
    
    Optional<Profile> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
