package com.customer.transaction.controller;

import com.customer.transaction.data.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.customer.transaction.data.model.Customer;

import java.util.Objects;

import static com.customer.transaction.controller.util.Parsers.tryParseInteger;

@RestController
@Slf4j
public class CustomerController {
    final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/v1/customers/{id}", method = RequestMethod.GET)
    private ResponseEntity<Customer> getCustomerByIdV1(@PathVariable String id) {
        log.info("Calling: getCustomerByIdV1 >> ".concat(id));

        val customer = customerService.findById(tryParseInteger(id, "id"));

        // TODO need to change the data coming from database into View form
        return ResponseEntity.ok(customer);
    }


}
