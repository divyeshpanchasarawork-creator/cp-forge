package com.divyesh.panchasara.cp_forge.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CodeforcesProfileDto(
        Long id,
        String handle,
        Integer rating,
        Integer maxRating,
        String rank,
        String maxRank,
        String avatarUrl,
        String organization,
        String country,
        OffsetDateTime lastSyncedAt,
        List<RatingHistoryDto> ratingHistory
) {
}
