package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import dev.decoder.rejectr.validation.NoPersonalDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoryRequest(
        @NotNull(message = "stage is required") Stage stage,
        @NotNull(message = "outcome is required") Outcome outcome,
        @NotBlank(message = "story must not be empty")
        @Size(max = 2000, message = "story must be at most 2000 characters")
        @NoPersonalDetails
        String body
) {
}
