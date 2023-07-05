package com.customer.transaction.data.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;


import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_info", schema = "bank")
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "balance")
    private double balance;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private Collection<Transaction> transactions;
}
