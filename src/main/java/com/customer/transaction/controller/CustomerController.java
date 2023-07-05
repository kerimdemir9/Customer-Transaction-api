package com.customer.transaction.controller;

import com.customer.transaction.controller.View.CustomerView;
import com.customer.transaction.controller.View.PagedData;
import com.customer.transaction.data.service.CustomerService;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.customer.transaction.data.model.Customer;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.customer.transaction.controller.util.Parsers.tryParseInteger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.builder;

@RestController
@Slf4j
public class CustomerController {
    final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/v1/customers/{id}", method = RequestMethod.GET)
    private ResponseEntity<CustomerView> getCustomerByIdV1(@PathVariable String id) {
        log.info("Calling: getCustomerByIdV1 >> ".concat(id));

        val customer = customerService.findById(tryParseInteger(id, "id"));

        return ResponseEntity.ok(mapCustomerToCustomerView(customer));
    }

    @RequestMapping(value = "v1/customers/find_all", method = RequestMethod.GET)
    private ResponseEntity<PagedData<CustomerView>> getAllCustomersV1(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllCustomersV1");

        val result = customerService.findAll(pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        return ResponseEntity.ok(mapPaged(result));
    }


    @RequestMapping(value = "/v1/customers/find_all_by_full_name/{fullName}", method = RequestMethod.GET)
    private ResponseEntity<PagedData<CustomerView>> getAllCustomersByFullName(
            @PathVariable String fullName,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: gelAllCustomersByFullName >> Full Name: ".concat(fullName));

        val result = customerService.findAllByFullName(fullName, pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        return ResponseEntity.ok(mapPaged(result));
    }

    private PagedData<CustomerView> mapPaged(GenericPagedModel<Customer> customers) {
        return PagedData.<CustomerView>builder()
                .totalElements(customers.getTotalElements())
                .totalPages(customers.totalPages)
                .numberOfElements(customers.getNumberOfElements())
                .content(mapCustomersCollectiontoCustomerViewList(customers.getContent()))
                .build();
    }

    private List<CustomerView> mapCustomersCollectiontoCustomerViewList(Collection<Customer> customers) {
        return customers.stream().map(this::mapCustomerToCustomerView).toList();
    }



    private CustomerView mapCustomerToCustomerView(Customer customer) {
        return CustomerView.builder()
                .phoneNumber(customer.getPhoneNumber())
                .balance(customer.getBalance())
                .fullName(customer.getFullName())
                .id(customer.getId())
                .build();
    }
}
