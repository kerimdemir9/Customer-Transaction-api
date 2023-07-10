package com.customer.transaction.validation.helper;
import com.customer.transaction.validation.SimpleValidation;
import com.customer.transaction.validation.Validation;
import java.util.Objects;
import static java.lang.String.format;
public class DoubleValidationHelpers {

    public static Validation<Double> lowerThan(Double max) {
        return SimpleValidation.from((i) -> i < max, format("must be lower than %s.", max));
    }

    public static Validation<Double> greaterThan(Double min) {
        return SimpleValidation.from((i) -> i > min, format("must be greater than %s.", min));
    }

    public static Validation<Double> doubleBetween(Double min, Double max) {
        return greaterThan(min).and(lowerThan(max));
    }
    public static Validation<Double> notNullDouble = SimpleValidation.from(Objects::nonNull, "must not be null.");

}
