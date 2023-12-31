package com.customer.transaction.data.validator;
import com.customer.transaction.data.model.TransactionModel;
import org.springframework.stereotype.Component;
import static com.customer.transaction.validation.Constants.CUSTOMER_ID_FIELD_FOR_VALIDATION;
import static com.customer.transaction.validation.Constants.TRANSACTION_AMOUNT_FIELD_FOR_VALIDATION;
import static com.customer.transaction.validation.helper.ObjectValidationHelpers.notNullObject;
import static com.customer.transaction.validation.helper.DoubleValidationHelpers.greaterThan;
import static com.customer.transaction.validation.helper.DoubleValidationHelpers.notNullDouble;



@Component
public class TransactionValidatorImpl implements TransactionValidator {
    @Override
    public void validate(TransactionModel model) {
        notNullObject.test(model.getCustomer().getId()).throwIfInvalid(CUSTOMER_ID_FIELD_FOR_VALIDATION);
        notNullDouble.and(greaterThan(0.0)).test(model.getAmount())
                .throwIfInvalid(TRANSACTION_AMOUNT_FIELD_FOR_VALIDATION);
    }
}
