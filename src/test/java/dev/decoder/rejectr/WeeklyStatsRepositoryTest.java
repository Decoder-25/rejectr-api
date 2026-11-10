package dev.decoder.rejectr;

import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WeeklyStatsRepositoryTest {

    private static final String MARKER = "test-weekly-stats";

    @Autowired
    private ShredRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM shreds WHERE device_hash = ?", MARKER);
    }

    @Test
    void countsOnlyOutcomesFromLastSevenDays() {
        Map<String, Long> before = counts();

        insertNow(Outcome.GHOSTED);
        insertNow(Outcome.GHOSTED);
        insertNow(Outcome.AUTOMATED_EMAIL);
        insertDaysAgo(Outcome.ON_HOLD, 8);

        Map<String, Long> after = counts();

        assertEquals(2, delta(before, after, Outcome.GHOSTED));
        assertEquals(1, delta(before, after, Outcome.AUTOMATED_EMAIL));
        assertEquals(0, delta(before, after, Outcome.ON_HOLD));
    }

    private void insertNow(Outcome outcome) {
        jdbcTemplate.update("""
                INSERT INTO shreds (stage, outcome, reason, created_at, device_hash)
                VALUES (?, ?, 'NO_REASON_GIVEN', now(), ?)
                """, Stage.APPLIED_NEVER_HEARD_BACK.name(), outcome.name(), MARKER);
    }

    private void insertDaysAgo(Outcome outcome, int days) {
        jdbcTemplate.update("""
                INSERT INTO shreds (stage, outcome, reason, created_at, device_hash)
                VALUES (?, ?, 'NO_REASON_GIVEN', now() - make_interval(days => ?), ?)
                """, Stage.APPLIED_NEVER_HEARD_BACK.name(), outcome.name(), days, MARKER);
    }

    private Map<String, Long> counts() {
        Map<String, Long> counts = new HashMap<>();
        for (ShredRepository.OutcomeCountProjection row : repository.countByOutcomeLast7Days()) {
            counts.put(row.getOutcome(), row.getTotal());
        }
        return counts;
    }

    private static long delta(Map<String, Long> before, Map<String, Long> after, Outcome outcome) {
        return after.getOrDefault(outcome.name(), 0L) - before.getOrDefault(outcome.name(), 0L);
    }
}
