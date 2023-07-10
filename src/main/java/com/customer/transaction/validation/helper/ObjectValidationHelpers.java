package com.customer.transaction.validation.helper;
import com.customer.transaction.validation.SimpleValidation;
import com.customer.transaction.validation.Validation;
import java.util.Objects;

public class ObjectValidationHelpers {
    public static Validation<Object> notNullObject = SimpleValidation.from(Objects::nonNull, "must not be null.");
}
