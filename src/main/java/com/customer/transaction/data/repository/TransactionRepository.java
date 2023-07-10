package com.customer.transaction.data.repository;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.model.TransactionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<TransactionModel, Integer> {
    Page<TransactionModel> findAllByCustomer(CustomerModel customerModel, Pageable pageable);
    Page<TransactionModel> findAllByCustomerAndCreatedBeforeAndCreatedAfter(CustomerModel customerModel, Date createdBefore, Date createdAfter, Pageable pageable);
}
