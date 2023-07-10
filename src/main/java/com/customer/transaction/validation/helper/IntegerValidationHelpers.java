package com.customer.transaction.validation.helper;


import com.customer.transaction.validation.SimpleValidation;
import com.customer.transaction.validation.Validation;

import java.util.Objects;

import static java.lang.String.format;

public class IntegerValidationHelpers {

    public static Validation<Integer> lowerThan(int max) {
        return SimpleValidation.from((i) -> i < max, format("must be lower than %s.", max));
    }

    public static Validation<Integer> greaterThan(int min) {
        return SimpleValidation.from((i) -> i > min, format("must be greater than %s.", min));
    }

    public static Validation<Integer> intBetween(int min, int max) {
        return greaterThan(min).and(lowerThan(max));
    }

    public static Validation<Integer> notNullInteger = SimpleValidation.from(Objects::nonNull, "must not be null.");

}
