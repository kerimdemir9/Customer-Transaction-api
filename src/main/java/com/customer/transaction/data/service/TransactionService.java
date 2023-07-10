package com.customer.transaction.data.service;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.model.TransactionModel;
import com.customer.transaction.data.repository.CustomerRepository;
import com.customer.transaction.data.repository.TransactionRepository;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.data.validator.CustomerValidator;
import com.customer.transaction.data.validator.TransactionValidator;
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
import java.util.Objects;

@Service
@Slf4j
public class TransactionService {
    final TransactionRepository transactionRepository;
    final CustomerRepository customerRepository;

    final CustomerValidator customerValidator;
    final TransactionValidator transactionValidator;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository, CustomerValidator customerValidator, TransactionValidator transactionValidator) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.customerValidator = customerValidator;
        this.transactionValidator = transactionValidator;
    }

    public TransactionModel findById(Integer id) {
        return getTransaction(id);
    }

    public GenericPagedModel<TransactionModel> findAll(int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<TransactionModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<TransactionModel> findAllByCustomer(CustomerModel customerModel, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            customerValidator.validate(customerModel);
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAllByCustomer(customerModel, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAllByCustomer(customerModel, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No customer:".concat(customerModel.toString()));
            }

            return GenericPagedModel.<TransactionModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public GenericPagedModel<TransactionModel> findAllByCustomerAndCreatedBeforeAndCreatedAfter(CustomerModel customerModel, Date createdBefore, Date createdAfter, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            customerValidator.validate(customerModel);
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? transactionRepository.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customerModel, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : transactionRepository.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customerModel, createdAfter, createdBefore, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if(result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transaction between dates: ".concat(createdBefore.toString()).concat(" | ").concat(createdAfter.toString()).concat(" of customer: ").concat(customerModel.toString()));
            }
            return GenericPagedModel.<TransactionModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public TransactionModel save(TransactionModel transactionModel) {
        try {
            transactionValidator.validate(transactionModel);
            if(transactionModel.getCustomer().getId() == null || transactionModel.getCustomer().getId() < 1) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Customer id doesn't exists");
            }
            log.info("Transaction saved: ". concat(transactionModel.toString()));
            return transactionRepository.save(transactionModel);
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public TransactionModel hardDelete(Integer id) {
        try {
            val transactionToHardDelete = getTransaction(id);

            transactionRepository.delete(transactionToHardDelete);

            return transactionToHardDelete;

        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    private TransactionModel getTransaction(Integer id) {
        try {
            if(Objects.isNull(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "transactionId must not be null");
            }
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
