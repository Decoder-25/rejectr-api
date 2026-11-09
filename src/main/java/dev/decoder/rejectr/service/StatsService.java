package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.WeeklyStatsResponse;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.shred.Outcome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatsService {

    private final ShredRepository repository;

    public StatsService(ShredRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public WeeklyStatsResponse weekly() {
        Map<Outcome, Long> counts = new LinkedHashMap<>();
        for (Outcome outcome : Outcome.values()) {
            counts.put(outcome, 0L);
        }

        long total = 0;
        for (ShredRepository.OutcomeCount row : repository.countByOutcomeLast7Days()) {
            long count = row.getTotal();
            counts.put(Outcome.valueOf(row.getOutcome()), count);
            total += count;
        }

        return new WeeklyStatsResponse(total, counts);
    }
}
