package com.divyesh.panchasara.cp_forge.repository;

import com.divyesh.panchasara.cp_forge.model.CodeforcesProfile;
import com.divyesh.panchasara.cp_forge.model.CodeforcesRatingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeforcesRatingHistoryRepository extends JpaRepository<CodeforcesRatingHistory, Long> {
    List<CodeforcesRatingHistory> findByProfileOrderByRatingUpdateTimeSecondsAsc(CodeforcesProfile profile);
    void deleteByProfile(CodeforcesProfile profile);
}
