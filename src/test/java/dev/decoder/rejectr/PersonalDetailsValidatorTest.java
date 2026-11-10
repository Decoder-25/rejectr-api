package dev.decoder.rejectr;

import dev.decoder.rejectr.validation.PersonalDetailsValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalDetailsValidatorTest {

    private final PersonalDetailsValidator validator = new PersonalDetailsValidator();

    @Test
    void acceptsNullAndBlank() {
        assertTrue(isValid(null));
        assertTrue(isValid(""));
        assertTrue(isValid("   "));
    }

    @Test
    void acceptsOrdinaryStory() {
        assertTrue(isValid("Five rounds over two months, then silence."));
        assertTrue(isValid("I was at home. Not sure what went wrong."));
        assertTrue(isValid("Applied in 2026 after 3 rounds."));
        assertTrue(isValid("Made it to the final round in 1999, 12 October."));
        assertTrue(isValid("Salary was 50,000 with 4 rounds."));
    }

    @Test
    void rejectsPlainEmail() {
        assertFalse(isValid("reach me at jane.doe@example.com please"));
        assertFalse(isValid("jane@example.co.uk"));
    }

    @Test
    void rejectsBracketedEmail() {
        assertFalse(isValid("jane [at] example [dot] com"));
        assertFalse(isValid("jane (at) example (dot) com"));
    }

    @Test
    void rejectsZeroWidthEmailBypass() {
        assertFalse(isValid("jane@exa\u200Bmple.com"));
    }

    @Test
    void rejectsFullwidthEmailBypass() {
        assertFalse(isValid("\uFF4A\uFF41\uFF4E\uFF45\uFF20\uFF45\uFF58\uFF41\uFF4D\uFF50\uFF4C\uFF45\uFF0E\uFF43\uFF4F\uFF4D"));
    }

    @Test
    void rejectsUkPhone() {
        assertFalse(isValid("call me on 07700 900123"));
        assertFalse(isValid("my number is (07700) 900-123"));
    }

    @Test
    void rejectsInternationalPhone() {
        assertFalse(isValid("call me on +44 7700 900123"));
    }

    @Test
    void doesNotFlagLongReferenceNumber() {
        assertTrue(isValid("reference 1234567890123456"));
    }

    private boolean isValid(String value) {
        return validator.isValid(value, null);
    }
}
