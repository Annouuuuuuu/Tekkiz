package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional <Role> findById(Long id);

    Optional <Role> findByRoleName(RoleType roleName);

}

