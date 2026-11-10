package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.OutcomeCount;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.service.StatsService;
import dev.decoder.rejectr.shred.Outcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private ShredRepository repository;

    private StatsService service;

    @BeforeEach
    void setUp() {
        service = new StatsService(repository);
    }

    @Test
    void returnsEveryOutcomeZeroFilledInEnumOrder() {
        when(repository.countByOutcomeLast7Days()).thenReturn(List.of(
                row(Outcome.GHOSTED, 4),
                row(Outcome.AUTOMATED_EMAIL, 2)
        ));

        List<OutcomeCount> counts = service.weekly();

        assertEquals(Outcome.values().length, counts.size());
        assertEquals(Outcome.GHOSTED, counts.get(0).type());
        assertEquals(4, counts.get(0).count());
        assertEquals(2, countOf(counts, Outcome.AUTOMATED_EMAIL));
        assertEquals(0, countOf(counts, Outcome.ON_HOLD));
    }

    private static long countOf(List<OutcomeCount> counts, Outcome outcome) {
        return counts.stream()
                .filter(count -> count.type() == outcome)
                .findFirst()
                .orElseThrow()
                .count();
    }

    private static ShredRepository.OutcomeCountProjection row(Outcome outcome, long total) {
        return new ShredRepository.OutcomeCountProjection() {
            @Override
            public String getOutcome() {
                return outcome.name();
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }
}
