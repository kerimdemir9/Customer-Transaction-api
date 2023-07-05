package com.customer.transaction.Data.service;

import com.customer.transaction.Data.model.Customer;
import com.customer.transaction.Data.repository.CustomerRepository;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
