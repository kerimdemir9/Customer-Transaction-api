package com.customer.transaction.data.repository;

import com.customer.transaction.data.model.CustomerLogModel;
import com.customer.transaction.data.model.TransactionModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.Date;


@Repository
public interface CustomerLogRepository extends PagingAndSortingRepository<CustomerLogModel, Integer> {
    Page<CustomerLogModel> findAllByCustomerId(Integer customerId, Pageable pageable);
    Page<CustomerLogModel> findAllByCustomerIdAndCreatedBeforeAndCreatedAfter(Integer customerId, Date createdBefore, Date createdAfter, Pageable pageable);
    Page<CustomerLogModel> findAllByCreatedBeforeAndCreatedAfter(Date createdBefore, Date createdAfter, Pageable pageable);

}
