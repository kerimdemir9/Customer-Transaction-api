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
@Table(name = "customer_log", schema = "bank")
@Entity
public class CustomerLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "old_version")
    private String oldVersion;

    @Column(name = "new_version")
    private String newVersion;

    @Column(name = "log_type")
    private String logType;

    @CreationTimestamp
    @Column(name = "created", insertable = false, updatable = false)
    private Date created;

    @Column(name = "customer_id")
    private Integer customerId;

}
