package dev.decoder.rejectr.service;

import dev.decoder.rejectr.dto.OutcomeCount;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.shred.Outcome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final ShredRepository repository;

    public StatsService(ShredRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OutcomeCount> weekly() {
        Map<Outcome, Long> counts = new LinkedHashMap<>();
        for (Outcome outcome : Outcome.values()) {
            counts.put(outcome, 0L);
        }

        for (ShredRepository.OutcomeCountProjection row : repository.countByOutcomeLast7Days()) {
            counts.put(Outcome.valueOf(row.getOutcome()), row.getTotal());
        }

        List<OutcomeCount> result = new ArrayList<>(counts.size());
        counts.forEach((outcome, count) -> result.add(new OutcomeCount(outcome, count)));
        return result;
    }
}
