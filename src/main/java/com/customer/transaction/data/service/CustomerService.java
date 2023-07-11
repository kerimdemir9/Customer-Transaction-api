package com.customer.transaction.data.service;

import com.customer.transaction.data.model.CustomerLogModel;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.repository.CustomerLogRepository;
import com.customer.transaction.data.repository.CustomerRepository;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.data.validator.CustomerValidator;
import com.customer.transaction.util.SortDirection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;


@Service
public class CustomerService {
    final CustomerRepository customerRepository;
    final CustomerLogRepository customerLogRepository;
    final CustomerValidator customerValidator;

    final ObjectMapper objectMapper;


    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerLogRepository customerLogRepository, CustomerValidator customerValidator, ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.customerLogRepository = customerLogRepository;
        this.customerValidator = customerValidator;
        this.objectMapper = objectMapper;
    }

    public CustomerModel findById(Integer id) {
        return getCustomer(id);
    }

    public GenericPagedModel<CustomerModel> findAll(int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data");
            }

            return GenericPagedModel.<CustomerModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    public GenericPagedModel<CustomerModel> findAllCustomersByBalanceBetween(double minBalance, double maxBalance, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAllCustomersByBalanceBetween(minBalance, maxBalance, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAllCustomersByBalanceBetween(minBalance, maxBalance, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Min:".concat(String.valueOf(minBalance)).concat(" Max:").concat(String.valueOf(maxBalance)));
            }
            return GenericPagedModel.<CustomerModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    public GenericPagedModel<CustomerModel> findAllByFullName(String fullName, int page, int size, String sortBy, SortDirection sortDirection) {
        try {
            val result = sortDirection.equals(SortDirection.Ascending)
                    ? customerRepository.findAllByFullName(fullName, PageRequest.of(page, size, Sort.by(sortBy).ascending()))
                    : customerRepository.findAllByFullName(fullName, PageRequest.of(page, size, Sort.by(sortBy).descending()));
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "fullName:".concat(fullName));
            }
            return GenericPagedModel.<CustomerModel>builder()
                    .totalElements(result.getTotalElements())
                    .numberOfElements(result.getNumberOfElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent())
                    .build();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    public CustomerModel save(CustomerModel customerModel) {
        try {
            customerValidator.validate(customerModel);
            if (customerRepository.existsByPhoneNumber(customerModel.getPhoneNumber()) && Objects.isNull(customerModel.getId())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "phoneNumber: ".concat(customerModel.getPhoneNumber()).concat(" already exists"));
            }
            if (Objects.nonNull(customerModel.getId())) {
                val found = customerRepository.findById(customerModel.getId()).orElse(null);
                if (Objects.nonNull(found)) {
                    if (objectMapper.writeValueAsString(customerModel).equals(objectMapper.writeValueAsString(found))) {
                        return customerModel;
                    }
                    val saved = customerRepository.save(customerModel);
                    val oldVersion = objectMapper.writeValueAsString(found);
                    val newVersion = objectMapper.writeValueAsString(saved);
                    customerLogRepository.save(CustomerLogModel.builder()
                            .oldVersion(oldVersion)
                            .newVersion(newVersion)
                            .customerId(saved.getId())
                            .created(new Date(Instant.now().toEpochMilli()))
                            .logType("updated")
                            .build());
                    return saved;
                }
            } else {
                val saved = customerRepository.save(customerModel);
                customerLogRepository.save(CustomerLogModel.builder()
                        .customerId(saved.getId())
                        .newVersion(objectMapper.writeValueAsString(saved))
                        .logType("inserted")
                        .oldVersion(null)
                        .created(new Date(Instant.now().toEpochMilli()))
                        .build());
                return saved;
            }
            return null;
        } catch (final DataIntegrityViolationException | JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public CustomerModel hardDelete(Integer id) {
        try {
            val customerToHardDelete = getCustomer(id);

            val oldVersion = objectMapper.writeValueAsString(customerToHardDelete);

            customerRepository.delete(customerToHardDelete);

            customerLogRepository.save(CustomerLogModel.builder()
                    .logType("deleted")
                    .oldVersion(oldVersion)
                    .newVersion(null)
                    .created(new Date(Instant.now().toEpochMilli()))
                    .customerId(id)
                    .build());

            return customerToHardDelete;
        } catch (final DataIntegrityViolationException | JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }


    private CustomerModel getCustomer(Integer id) {
        try {
            if (Objects.isNull(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "customerId must not be null");
            }
            val result = customerRepository.findById(id);
            if (result.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No customer with id: ".concat(id.toString()));
            }
            return result.get();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    public void hardDeleteAll() {
        try {
            customerRepository.deleteAll();
        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
