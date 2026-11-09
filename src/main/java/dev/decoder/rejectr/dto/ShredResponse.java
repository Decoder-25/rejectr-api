package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Reason;
import dev.decoder.rejectr.shred.Stage;

import java.time.Instant;
import java.util.UUID;

public record ShredResponse(
        UUID id,
        Stage stage,
        Outcome outcome,
        Reason reason,
        Instant createdAt
) {
    public static ShredResponse from(Shred shred) {
        return new ShredResponse(
                shred.getId(),
                shred.getStage(),
                shred.getOutcome(),
                shred.getReason(),
                shred.getCreatedAt()
        );
    }
}
