package com.customer.transaction.data.service;

import com.customer.transaction.data.model.CustomerLogModel;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.model.TransactionModel;
import com.customer.transaction.data.repository.CustomerLogRepository;
import com.customer.transaction.data.repository.CustomerRepository;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@Slf4j
public class CustomerLogService {
    final CustomerLogRepository customerLogRepository;
    final CustomerRepository customerRepository;

    @Autowired
    public CustomerLogService(CustomerLogRepository customerLogRepository, CustomerRepository customerRepository) {
        this.customerLogRepository = customerLogRepository;
        this.customerRepository = customerRepository;
    }

    public CustomerLogModel findById(Integer id) {
        return getLog(id);
    }

    public GenericPagedModel<CustomerLogModel> findAllByCustomerId(Integer customerId, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerLogRepository.findAllByCustomerId(customerId, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerLogRepository.findAllByCustomerId(customerId, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<CustomerLogModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<CustomerLogModel> findAllByCreatedBeforeAndCreatedAfter(Date createdBefore, Date createdAfter, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerLogRepository.findAllByCreatedBeforeAndCreatedAfter(createdBefore, createdAfter, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerLogRepository.findAllByCreatedBeforeAndCreatedAfter(createdBefore, createdAfter, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<CustomerLogModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<CustomerLogModel> findAllByCustomerIdAndCreatedBeforeAndCreatedAfter(
            Integer customerId,
            Date createdBefore,
            Date createdAfter,
            int page, int size,
            String sortBy,
            SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerLogRepository.findAllByCustomerIdAndCreatedBeforeAndCreatedAfter(customerId, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerLogRepository.findAllByCustomerIdAndCreatedBeforeAndCreatedAfter(customerId, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No log between dates: ".concat(createdBefore.toString()).concat(" | ").concat(createdAfter.toString()).concat(" of customer with id: ").concat(customerId.toString()));
            }
            return GenericPagedModel.<CustomerLogModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    private CustomerLogModel getLog(Integer id) {
        try {
            val result = customerLogRepository.findById(id);
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "log with id: ".concat(id.toString()).concat(" not found"));
            }
            return result.get();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public CustomerLogModel save(CustomerLogModel customerLogModel) {
        try {
            log.info("Log recorded: ".concat(customerLogModel.toString()));
            return customerLogRepository.save(customerLogModel);
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public void hardDeleteAll() {
        try {
            customerLogRepository.deleteAll();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
