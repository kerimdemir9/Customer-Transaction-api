package com.customer.transaction.util;

import com.customer.transaction.data.model.Customer;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPagedData {
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private List<Customer> content;
}
