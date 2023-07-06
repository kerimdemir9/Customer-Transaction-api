package com.customer.transaction.controller.View;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerView {
    private Integer id;
    private String fullName;
    private String phoneNumber;
    private Double balance;
}
