package dev.decoder.rejectr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoryRequest(
        @NotBlank(message = "story must not be empty")
        @Size(max = 2000, message = "story must be at most 2000 characters")
        String body
) {
}
