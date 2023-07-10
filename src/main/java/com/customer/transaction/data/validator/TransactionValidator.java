package com.customer.transaction.data.validator;

import com.customer.transaction.data.model.TransactionModel;

public interface TransactionValidator {
    void validate(TransactionModel model);

}
