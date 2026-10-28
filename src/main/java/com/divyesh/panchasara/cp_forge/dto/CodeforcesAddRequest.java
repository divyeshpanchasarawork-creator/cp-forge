package com.divyesh.panchasara.cp_forge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CodeforcesAddRequest(
        @NotBlank(message = "Handle is required")
        @Size(min = 1, max = 50, message = "Handle must be between 1 and 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "Handle can only contain letters, numbers, dots, underscores and hyphens")
        String handle
) {
}
