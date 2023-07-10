package com.customer.transaction.data.validator;
import com.customer.transaction.data.model.CustomerModel;
import org.springframework.stereotype.Component;
import static com.customer.transaction.validation.Constants.CUSTOMER_FULL_NAME_FIELD_FOR_VALIDATION;
import static com.customer.transaction.validation.Constants.CUSTOMER_PHONE_NUMBER_FIELD_FOR_VALIDATION;
import static com.customer.transaction.validation.Constants.CUSTOMER_BALANCE_FIELD_FOR_VALIDATION;
import static com.customer.transaction.validation.helper.DoubleValidationHelpers.notNullDouble;
import static com.customer.transaction.validation.helper.StringValidationHelpers.notBlank;
import static com.customer.transaction.validation.helper.DoubleValidationHelpers.greaterThan;

@Component
public class CustomerValidatorImpl implements CustomerValidator {
    @Override
    public void validate(CustomerModel model) {
        notBlank.test(model.getFullName()).throwIfInvalid(CUSTOMER_FULL_NAME_FIELD_FOR_VALIDATION);
        notBlank.test(model.getPhoneNumber()).throwIfInvalid(CUSTOMER_PHONE_NUMBER_FIELD_FOR_VALIDATION);
        notNullDouble.and(greaterThan(0.0)).test(model.getBalance()).throwIfInvalid(CUSTOMER_BALANCE_FIELD_FOR_VALIDATION);
    }
}
