package com.customer.transaction.controller.View;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionView {
    private Integer id;
    private Double amount;
    private Date created;
    private Integer customerId;
}
