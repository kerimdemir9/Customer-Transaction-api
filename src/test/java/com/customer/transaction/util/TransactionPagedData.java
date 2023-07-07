package com.customer.transaction.util;

import com.customer.transaction.data.model.Transaction;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPagedData {
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private List<Transaction> content;
}
