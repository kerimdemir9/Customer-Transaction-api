package com.customer.transaction.controller.View;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionViewPagedData {
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private List<TransactionView> content;
}
