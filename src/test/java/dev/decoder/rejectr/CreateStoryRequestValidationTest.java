package dev.decoder.rejectr;

import dev.decoder.rejectr.dto.CreateStoryRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        return validator.validate(new CreateStoryRequest(body));
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
}
