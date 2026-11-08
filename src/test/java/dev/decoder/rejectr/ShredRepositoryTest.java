package dev.decoder.rejectr;

import dev.decoder.rejectr.entity.Shred;
import dev.decoder.rejectr.repository.ShredRepository;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Reason;
import dev.decoder.rejectr.shred.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ShredRepositoryTest {

    @Autowired
    private ShredRepository repository;

    @Test
    void savesOneShred() {
        Shred shred = new Shred();
        shred.setStage(Stage.APPLIED_NEVER_HEARD_BACK);
        shred.setOutcome(Outcome.GHOSTED);
        shred.setReason(Reason.NO_REASON_GIVEN);

        Shred saved = repository.saveAndFlush(shred);

        System.out.println("Saved shred id = " + saved.getId());
        assertNotNull(saved.getId());
    }
}
