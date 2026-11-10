package dev.decoder.rejectr.dto;

import dev.decoder.rejectr.shred.Outcome;

public record OutcomeCount(Outcome type, long count) {
}
