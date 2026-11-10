package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.entity.Story;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import dev.decoder.rejectr.shred.Status;

import java.time.Instant;
import java.util.UUID;

public record StoryResponse(
        UUID id,
        Stage stage,
        Outcome outcome,
        String body,
        Status status,
        Instant createdAt
) {
    public static StoryResponse from(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getStage(),
                story.getOutcome(),
                story.getBody(),
                story.getStatus(),
                story.getCreatedAt()
        );
    }
}
