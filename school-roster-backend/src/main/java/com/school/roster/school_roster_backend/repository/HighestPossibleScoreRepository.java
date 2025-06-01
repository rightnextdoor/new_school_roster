package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HighestPossibleScoreRepository extends JpaRepository<HighestPossibleScore, Long> {
    Optional<HighestPossibleScore> findByRosterId(Long rosterId);
}
