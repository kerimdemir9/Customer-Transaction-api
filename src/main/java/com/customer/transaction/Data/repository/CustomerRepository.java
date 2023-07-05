package com.customer.transaction.Data.repository;

import com.customer.transaction.Data.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
    Page<Customer> findAllByMaxGreaterThanMinLessThan(Double Max, Double Min, Pageable pageable);

    Page<Customer> findAllByFullName(String fullName, Pageable pageable);
}

