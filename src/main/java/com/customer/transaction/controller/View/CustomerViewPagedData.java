package com.customer.transaction.controller.View;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerViewPagedData {
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private List<CustomerView> content;
}
