package com.customer.transaction.data.repository;
import com.customer.transaction.data.model.CustomerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<CustomerModel, Integer> {
    Page<CustomerModel> findAllByFullName(String fullName, Pageable pageable);

    Page<CustomerModel> findAllCustomersByBalanceBetween(double minBalance, double maxBalance, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);
}

