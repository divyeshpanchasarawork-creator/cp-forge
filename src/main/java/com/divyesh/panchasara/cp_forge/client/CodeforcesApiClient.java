package com.divyesh.panchasara.cp_forge.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class CodeforcesApiClient {

    private static final Logger log = LoggerFactory.getLogger(CodeforcesApiClient.class);
    private final RestClient codeforcesRestClient;

    public CodeforcesApiClient(RestClient codeforcesRestClient) {
        this.codeforcesRestClient = codeforcesRestClient;
    }

    public CodeforcesUserResult fetchUser(String handle) {
        try {
            CodeforcesUserResponse response = codeforcesRestClient.get()
                    .uri("/user.info?handles={handle}", handle)
                    .retrieve()
                    .body(CodeforcesUserResponse.class);

            if (response == null || !"OK".equals(response.status) || response.result == null || response.result.isEmpty()) {
                throw new CodeforcesApiException("Failed to fetch user info for handle: " + handle);
            }
            return response.result.get(0);
        } catch (RestClientException e) {
            log.error("Error fetching Codeforces user info for handle: {}", handle, e);
            throw new CodeforcesApiException("Unable to fetch user info from Codeforces", e);
        }
    }

    public List<CodeforcesRatingChange> fetchRatingHistory(String handle) {
        try {
            CodeforcesRatingResponse response = codeforcesRestClient.get()
                    .uri("/user.rating?handle={handle}", handle)
                    .retrieve()
                    .body(CodeforcesRatingResponse.class);

            if (response == null || !"OK".equals(response.status) || response.result == null) {
                throw new CodeforcesApiException("Failed to fetch rating history for handle: " + handle);
            }
            return response.result;
        } catch (RestClientException e) {
            log.error("Error fetching Codeforces rating history for handle: {}", handle, e);
            throw new CodeforcesApiException("Unable to fetch rating history from Codeforces", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeforcesUserResponse(String status, List<CodeforcesUserResult> result) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeforcesUserResult(
            String handle,
            Integer rating,
            Integer maxRating,
            String rank,
            String maxRank,
            String avatar,
            String titlePhoto,
            String organization,
            String country
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeforcesRatingResponse(String status, List<CodeforcesRatingChange> result) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeforcesRatingChange(
            Integer contestId,
            String contestName,
            Integer rank,
            Long ratingUpdateTimeSeconds,
            Integer oldRating,
            Integer newRating
    ) {
    }

    public static class CodeforcesApiException extends RuntimeException {
        public CodeforcesApiException(String message) {
            super(message);
        }

        public CodeforcesApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
