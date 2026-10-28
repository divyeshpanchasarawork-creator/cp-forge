package com.divyesh.panchasara.cp_forge.dto;

public record RatingHistoryDto(
        Integer contestId,
        String contestName,
        Integer rank,
        Long ratingUpdateTimeSeconds,
        Integer oldRating,
        Integer newRating
) {
}
