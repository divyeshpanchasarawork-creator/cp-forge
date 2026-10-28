package com.divyesh.panchasara.cp_forge.repository;

import com.divyesh.panchasara.cp_forge.model.CodeforcesProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeforcesProfileRepository extends JpaRepository<CodeforcesProfile, Long> {
    Optional<CodeforcesProfile> findByHandleIgnoreCase(String handle);
}
