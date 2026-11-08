package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Reason;
import dev.decoder.rejectr.shred.Stage;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateShredRequest(
        @NotNull(message = "stage is required") Stage stage,
        @NotNull(message = "outcome is required") Outcome outcome,
        Reason reason,
        UUID deviceId
) {
}
