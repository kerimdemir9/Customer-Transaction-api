package com.customer.transaction.data.validator;

import com.customer.transaction.data.model.Transaction;

public interface TransactionValidator {
    void validate(Transaction model);

}
