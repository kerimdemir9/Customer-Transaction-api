package com.customer.transaction.data.service;

import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.model.Transaction;
import com.customer.transaction.data.repository.CustomerRepository;
import com.customer.transaction.data.repository.TransactionRepository;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@Slf4j
public class TransactionService {
    final TransactionRepository transactionRepository;
    final CustomerRepository customerRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public Transaction findById(Integer id) {
        return getTransaction(id);
    }

    public GenericPagedModel<Transaction> findAll(int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<Transaction>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<Transaction> findAllByCustomer(Customer customer, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAllByCustomer(customer, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAllByCustomer(customer, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No customer:".concat(customer.toString()));
            }

            return GenericPagedModel.<Transaction>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<Transaction> findAllByCustomerAndCreatedBeforeAndCreatedAfter(Customer customer, Date createdBefore, Date createdAfter, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customer, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customer, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if(result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transaction between dates: ".concat(createdBefore.toString()).concat(" | ").concat(createdAfter.toString()).concat(" of customer: ").concat(customer.toString()));
            }
            return GenericPagedModel.<Transaction>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public Transaction save(Transaction transaction) {
        try {
            if(transaction.getCustomer().getId() == null || transaction.getCustomer().getId() < 1) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Customer id doesn't exists");
            }
            log.info("Transaction saved: ". concat(transaction.toString()));
            return transactionRepository.save(transaction);
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public Transaction hardDelete(Integer id) {
        try {
            val transactionToHardDelete = getTransaction(id);

            transactionRepository.delete(transactionToHardDelete);

            return transactionToHardDelete;

        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    private Transaction getTransaction(Integer id) {
        try {
            val result = transactionRepository.findById(id);
            if(result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transactionId:".concat(id.toString()));
            }
            return result.get();
        }
        catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public void hardDeleteAll() {
        try {
            transactionRepository.deleteAll();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
