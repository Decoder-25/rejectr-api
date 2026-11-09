package dev.decoder.rejectr.repository;

import dev.decoder.rejectr.entity.Shred;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ShredRepository extends JpaRepository<Shred, UUID> {

    @Query(value = """
            SELECT outcome, COUNT(*) AS total
            FROM shreds
            WHERE created_at >= now() - INTERVAL '7 days'
            GROUP BY outcome
            """, nativeQuery = true)
    List<OutcomeCount> countByOutcomeLast7Days();

    interface OutcomeCount {
        String getOutcome();

        long getTotal();
    }
}
