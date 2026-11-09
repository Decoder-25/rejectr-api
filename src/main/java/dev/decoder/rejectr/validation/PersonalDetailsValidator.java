package dev.decoder.rejectr.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class PersonalDetailsValidator implements ConstraintValidator<NoPersonalDetails, String> {

    private static final Pattern ZERO_WIDTH = Pattern.compile("[\\u200B-\\u200D\\uFEFF]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern EMAIL = Pattern.compile(
            "(?i)\\b[\\w.%+-]+\\s*(?:@|\\[at\\]|\\(at\\))\\s*[\\w-]+"
                    + "(?:\\s*(?:\\.|\\[dot\\]|\\(dot\\))\\s*[\\w-]+)*"
                    + "\\s*(?:\\.|\\[dot\\]|\\(dot\\))\\s*[a-z]{2,}\\b");
    private static final Pattern PHONE = Pattern.compile("(?<!\\d)\\+?\\d[\\d\\s().-]{7,}\\d(?!\\d)");
    private static final Pattern DIGIT = Pattern.compile("\\d");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String text = normalize(value);
        if (EMAIL.matcher(text).find()) {
            return false;
        }
        return !containsPhone(text);
    }

    private static String normalize(String value) {
        String text = Normalizer.normalize(value, Normalizer.Form.NFKC);
        text = ZERO_WIDTH.matcher(text).replaceAll("");
        return WHITESPACE.matcher(text).replaceAll(" ").trim();
    }

    private static boolean containsPhone(String text) {
        var matcher = PHONE.matcher(text);
        while (matcher.find()) {
            long digits = DIGIT.matcher(matcher.group()).results().count();
            if (digits >= 9 && digits <= 15) {
                return true;
            }
        }
        return false;
    }
}
