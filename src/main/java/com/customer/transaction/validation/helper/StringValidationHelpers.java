package com.customer.transaction.validation.helper;
import org.apache.commons.lang3.StringUtils;
import com.customer.transaction.validation.SimpleValidation;
import com.customer.transaction.validation.Validation;

import java.util.Objects;

import static java.lang.String.format;

public class StringValidationHelpers {
    public static Validation<String> notNull = SimpleValidation.from(Objects::nonNull, "must not be null.");

    public static Validation<String> notBlank = SimpleValidation.from(StringUtils::isNotBlank,
            "must not be empty.");

    public static Validation<String> notValidUUID = SimpleValidation.from(Parsers::notValidUUID, "must be valid UUID.");

    public static Validation<String> notValidUUIDOnHeader = SimpleValidation.from(Parsers::notValidUUID, "on the header must be valid UUID.");

    public static Validation<String> moreThan(int size) {
        return SimpleValidation.from((s) -> s.length() >= size, format("must have more than %s chars.", size));
    }

    public static Validation<String> lessThan(int size) {
        return SimpleValidation.from((s) -> s.length() <= size, format("must have less than %s chars.", size));
    }

    public static Validation<String> between(int minSize, int maxSize) {
        return moreThan(minSize).and(lessThan(maxSize));
    }

    public static Validation<String> contains(String c) {
        return SimpleValidation.from((s) -> s.contains(c), format("must contain %s", c));
    }
}
