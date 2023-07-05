package com.customer.transaction.data.service;

import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.repository.CustomerRepository;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class CustomerService {
    final CustomerRepository customerRepository;

    // TODO: add validator

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(Integer id) {
        return getCustomer(id);
    }

    public GenericPagedModel<Customer> findAll(int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<Customer>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    public GenericPagedModel<Customer> findAllCustomersByBalanceBetween(double minBalance, double maxBalance, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAllCustomersByBalanceBetween(minBalance, maxBalance, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAllCustomersByBalanceBetween(minBalance, maxBalance, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Min:".concat(String.valueOf(minBalance)).concat(" Max:").concat(String.valueOf(maxBalance)));
            }
            return GenericPagedModel.<Customer>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    public GenericPagedModel<Customer> findAllByFullName(String fullName, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAllByFullName(fullName, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAllByFullName(fullName, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "fullName:".concat(fullName));
            }
            return GenericPagedModel.<Customer>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
    } catch (final DataIntegrityViolationException ex) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
    }
}

    private Customer getCustomer(Integer id) {
        try {
            val result = customerRepository.findById(id);
            if(result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customerId:".concat(id.toString()));
            }
            return result.get();
        }
        catch(final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }
}
