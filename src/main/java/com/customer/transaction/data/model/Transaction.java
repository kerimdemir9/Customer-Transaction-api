package com.customer.transaction.data.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction", schema = "bank")
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(name = "amount")
    private Double amount;

    @CreationTimestamp
    @Column(insertable = false, updatable = false)
    private Date created;

    @ManyToOne
    @JsonBackReference
    private Customer customer;

}


/*
    id INT NOT NULL primary key,
    amount Double NOT NULL,
    account_id INT NOT NULL,
	FOREIGN KEY (customer_info_id) REFERENCES customer_info (id)
    ON DELETE CASCADE ON UPDATE CASCADE
*/