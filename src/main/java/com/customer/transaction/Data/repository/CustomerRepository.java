package com.customer.transaction.Data.repository;

import com.customer.transaction.Data.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

    //Page<Customer> findAllByMaxGreaterThanMinLessThan(double max, double min, Pageable pageable);

    Page<Customer> findAllByFullName(String fullName, Pageable pageable);
}

