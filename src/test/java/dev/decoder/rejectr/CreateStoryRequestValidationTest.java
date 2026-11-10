package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import dev.decoder.rejectr.shred.Outcome;
import dev.decoder.rejectr.shred.Stage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateStoryRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private Set<ConstraintViolation<CreateStoryRequest>> violations(String body) {
        return validator.validate(new CreateStoryRequest(Stage.FINAL_ROUND, Outcome.GHOSTED, body));
    }

    @Test
    void rejectsMissingStage() {
        Set<ConstraintViolation<CreateStoryRequest>> result =
                validator.validate(new CreateStoryRequest(null, Outcome.GHOSTED, "A story."));

        assertEquals(1, result.size());
        assertEquals("stage is required", result.iterator().next().getMessage());
    }

    @Test
    void rejectsMissingOutcome() {
        Set<ConstraintViolation<CreateStoryRequest>> result =
                validator.validate(new CreateStoryRequest(Stage.FINAL_ROUND, null, "A story."));

        assertEquals(1, result.size());
        assertEquals("outcome is required", result.iterator().next().getMessage());
    }

    @Test
    void acceptsValidStory() {
        assertTrue(violations("I was rejected after the final round.").isEmpty());
    }

    @Test
    void acceptsExactlyMaxLength() {
        assertTrue(violations("a".repeat(2000)).isEmpty());
    }

    @Test
    void rejectsNull() {
        Set<ConstraintViolation<CreateStoryRequest>> result = violations(null);

        assertEquals(1, result.size());
        assertEquals("story must not be empty", result.iterator().next().getMessage());
    }

    @Test
    void rejectsEmpty() {
        assertEquals(1, violations("").size());
    }

    @Test
    void rejectsWhitespaceOnly() {
        Set<ConstraintViolation<CreateStoryRequest>> result = violations("   \n\t ");

        assertEquals(1, result.size());
        assertEquals("story must not be empty", result.iterator().next().getMessage());
    }

    @Test
    void rejectsTooLong() {
        Set<ConstraintViolation<CreateStoryRequest>> result = violations("a".repeat(2001));

        assertEquals(1, result.size());
        assertEquals("story must be at most 2000 characters", result.iterator().next().getMessage());
    }

    @Test
    void rejectsPlainEmail() {
        Set<ConstraintViolation<CreateStoryRequest>> result =
                violations("reach me at jane.doe@example.com please");

        assertEquals(1, result.size());
        assertEquals("remove email addresses and phone numbers before sharing",
                result.iterator().next().getMessage());
    }

    @Test
    void rejectsBracketedEmail() {
        assertFalse(violations("jane [at] example [dot] com").isEmpty());
    }

    @Test
    void rejectsZeroWidthEmail() {
        assertFalse(violations("jane@exa\u200Bmple.com").isEmpty());
    }

    @Test
    void rejectsUkPhone() {
        assertFalse(violations("call me on 07700 900123").isEmpty());
    }

    @Test
    void rejectsInternationalPhone() {
        assertFalse(violations("call me on +44 7700 900123").isEmpty());
    }

    @Test
    void acceptsSentenceWithBareAt() {
        assertTrue(violations("I was at home. Not sure what went wrong.").isEmpty());
    }

    @Test
    void acceptsYearsAndRoundCounts() {
        assertTrue(violations("Applied in 2026 after 3 rounds.").isEmpty());
    }
}
