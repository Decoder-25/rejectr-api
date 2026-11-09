package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.WeeklyStatsResponse;
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
    void zeroFillsEveryOutcomeAndSumsTotal() {
        when(repository.countByOutcomeLast7Days()).thenReturn(List.of(
                row(Outcome.GHOSTED, 4),
                row(Outcome.AUTOMATED_EMAIL, 2)
        ));

        WeeklyStatsResponse response = service.weekly();

        assertEquals(Outcome.values().length, response.byOutcome().size());
        assertEquals(4, response.byOutcome().get(Outcome.GHOSTED));
        assertEquals(2, response.byOutcome().get(Outcome.AUTOMATED_EMAIL));
        assertEquals(0, response.byOutcome().get(Outcome.ON_HOLD));
        assertEquals(6, response.total());
    }

    private static ShredRepository.OutcomeCount row(Outcome outcome, long total) {
        return new ShredRepository.OutcomeCount() {
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
