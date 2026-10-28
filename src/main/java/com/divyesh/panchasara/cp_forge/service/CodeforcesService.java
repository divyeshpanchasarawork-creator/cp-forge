package com.divyesh.panchasara.cp_forge.service;

import com.divyesh.panchasara.cp_forge.client.CodeforcesApiClient;
import com.divyesh.panchasara.cp_forge.dto.CodeforcesProfileDto;
import com.divyesh.panchasara.cp_forge.dto.RatingHistoryDto;
import com.divyesh.panchasara.cp_forge.model.CodeforcesProfile;
import com.divyesh.panchasara.cp_forge.model.CodeforcesRatingHistory;
import com.divyesh.panchasara.cp_forge.repository.CodeforcesProfileRepository;
import com.divyesh.panchasara.cp_forge.repository.CodeforcesRatingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CodeforcesService {

    private final CodeforcesApiClient apiClient;
    private final CodeforcesProfileRepository profileRepository;
    private final CodeforcesRatingHistoryRepository ratingHistoryRepository;

    public CodeforcesService(CodeforcesApiClient apiClient,
                             CodeforcesProfileRepository profileRepository,
                             CodeforcesRatingHistoryRepository ratingHistoryRepository) {
        this.apiClient = apiClient;
        this.profileRepository = profileRepository;
        this.ratingHistoryRepository = ratingHistoryRepository;
    }

    @Transactional
    public CodeforcesProfileDto addOrRefreshProfile(String handle) {
        String normalizedHandle = handle.trim();
        CodeforcesApiClient.CodeforcesUserResult user = apiClient.fetchUser(normalizedHandle);
        List<CodeforcesApiClient.CodeforcesRatingChange> ratingChanges = apiClient.fetchRatingHistory(normalizedHandle);

        Optional<CodeforcesProfile> existingOpt = profileRepository.findByHandleIgnoreCase(normalizedHandle);
        CodeforcesProfile profile = existingOpt.orElseGet(CodeforcesProfile::new);
        if (existingOpt.isEmpty()) {
            profile.setHandle(user.handle());
        } else {
            // Keep existing handle casing as stored
            if (profile.getHandle() == null || profile.getHandle().isBlank()) {
                profile.setHandle(user.handle());
            }
        }

        profile.setRating(user.rating());
        profile.setMaxRating(user.maxRating());
        profile.setRank(user.rank());
        profile.setMaxRank(user.maxRank());
        String avatar = user.avatar() != null ? user.avatar() : user.titlePhoto();
        profile.setAvatarUrl(avatar);
        profile.setOrganization(user.organization());
        profile.setCountry(user.country());
        profile.setLastSyncedAt(OffsetDateTime.now());

        profile = profileRepository.save(profile);

        // Never replace with empty data - only update if we got data
        if (ratingChanges != null && !ratingChanges.isEmpty()) {
            ratingHistoryRepository.deleteByProfile(profile);
            List<CodeforcesRatingHistory> histories = new ArrayList<>();
            for (CodeforcesApiClient.CodeforcesRatingChange change : ratingChanges) {
                CodeforcesRatingHistory history = new CodeforcesRatingHistory();
                history.setProfile(profile);
                history.setContestId(change.contestId());
                history.setContestName(change.contestName());
                history.setRank(change.rank());
                history.setRatingUpdateTimeSeconds(change.ratingUpdateTimeSeconds());
                history.setOldRating(change.oldRating());
                history.setNewRating(change.newRating());
                histories.add(history);
            }
            ratingHistoryRepository.saveAll(histories);
            profile.setRatingHistories(histories);
        } else {
            // Keep existing history if fetch returned empty
            List<CodeforcesRatingHistory> existingHistories = ratingHistoryRepository.findByProfileOrderByRatingUpdateTimeSecondsAsc(profile);
            profile.setRatingHistories(existingHistories);
        }

        return toDto(profile);
    }

    @Transactional(readOnly = true)
    public List<CodeforcesProfileDto> listProfiles() {
        return profileRepository.findAll().stream()
                .map(this::toDtoWithHistory)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CodeforcesProfileDto getProfile(String handle) {
        CodeforcesProfile profile = profileRepository.findByHandleIgnoreCase(handle.trim())
                .orElseThrow(() -> new CodeforcesNotFoundException("Profile not found"));
        return toDtoWithHistory(profile);
    }

    @Transactional
    public CodeforcesProfileDto refreshProfile(String handle) {
        return addOrRefreshProfile(handle);
    }

    private CodeforcesProfileDto toDto(CodeforcesProfile profile) {
        List<CodeforcesRatingHistory> histories = ratingHistoryRepository.findByProfileOrderByRatingUpdateTimeSecondsAsc(profile);
        return toDtoFromEntities(profile, histories);
    }

    private CodeforcesProfileDto toDtoWithHistory(CodeforcesProfile profile) {
        return toDtoFromEntities(profile, profile.getRatingHistories());
    }

    private CodeforcesProfileDto toDtoFromEntities(CodeforcesProfile profile, List<CodeforcesRatingHistory> histories) {
        List<RatingHistoryDto> historyDtos = histories == null ? List.of() : histories.stream()
                .map(h -> new RatingHistoryDto(
                        h.getContestId(),
                        h.getContestName(),
                        h.getRank(),
                        h.getRatingUpdateTimeSeconds(),
                        h.getOldRating(),
                        h.getNewRating()
                ))
                .collect(Collectors.toList());

        return new CodeforcesProfileDto(
                profile.getId(),
                profile.getHandle(),
                profile.getRating(),
                profile.getMaxRating(),
                profile.getRank(),
                profile.getMaxRank(),
                profile.getAvatarUrl(),
                profile.getOrganization(),
                profile.getCountry(),
                profile.getLastSyncedAt(),
                historyDtos
        );
    }

    public static class CodeforcesNotFoundException extends RuntimeException {
        public CodeforcesNotFoundException(String message) {
            super(message);
        }
    }
}
