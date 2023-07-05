package com.customer.transaction.Data.repository;

import com.customer.transaction.Data.model.Customer;
import com.customer.transaction.Data.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.Date;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Integer> {
    Page<Transaction> findAllByCustomer(Customer customer, Pageable pageable);
    Page<Transaction> findAllByCustomerAndCreatedBeforeAndCreatedAfter(Customer customer, Date createdBefore, Date createdAfter, Pageable pageable);

    //Page<Transaction> findOneByMaxGreaterThanAndMinLessThan(Double Max, Double Min, Pageable pageable);
}
