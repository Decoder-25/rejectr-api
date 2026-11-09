package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.shred.Outcome;

import java.util.Map;

public record WeeklyStatsResponse(
        long total,
        Map<Outcome, Long> byOutcome
) {
}
