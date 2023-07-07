package com.customer.transaction.controller;

import com.customer.transaction.controller.View.TransactionViewPagedData;
import com.customer.transaction.controller.View.TransactionView;
import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.model.Transaction;
import com.customer.transaction.data.service.CustomerService;
import com.customer.transaction.data.service.TransactionService;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.customer.transaction.controller.util.Parsers.tryParseInteger;
import static com.customer.transaction.controller.util.Parsers.tryParseLong;

@RestController
@Slf4j
public class TransactionController {

    final TransactionService transactionService;
    final CustomerService customerService;

    @Autowired
    public TransactionController(TransactionService transactionService, CustomerService customerService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
    }

    @RequestMapping(value = "/v1/transactions/{id}", method = RequestMethod.GET)
    private ResponseEntity<TransactionView> getTransactionByIdV1(@PathVariable String id) {
        log.info("Calling: getTransactionByIdV1 >> ".concat(id));

        val result = transactionService.findById(tryParseInteger(id, "id"));

        return ResponseEntity.ok(mapTransactionToTransactionView(result));
    }

    @RequestMapping(value = "/v1/transactions/find_all", method = RequestMethod.GET)
    private ResponseEntity<TransactionViewPagedData> getAllTransactionsV1(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllTransactionsV1");

        val result = transactionService.findAll(pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        return ResponseEntity.ok(mapPaged(result));
    }

    @RequestMapping(value = "/v1/transactions/find_all_by_customer", method = RequestMethod.GET)
    private ResponseEntity<TransactionViewPagedData> getAllTransactionsByCustomerV1(
            @RequestBody Customer customer,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllTransactionsByCustomerV1 >> Customer fullName: ".concat(customer.getFullName()));

        val result = transactionService.findAllByCustomer(customer, pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        return ResponseEntity.ok(mapPaged(result));
    }


    @RequestMapping(value = "/v1/transactions/find_all_by_customer_created_before_created_after", method = RequestMethod.GET)
    private ResponseEntity<TransactionViewPagedData> getAllByCustomerAndCreatedBeforeAndCreatedAfterV1(
            @RequestBody Customer customer  ,
            @RequestParam(defaultValue = "") String createdBefore,
            @RequestParam(defaultValue = "") String createdAfter,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllByCustomerAndCreatedBeforeAndCreatedAfterV1 >> Customer: "
                .concat(customer.toString())
                .concat(" | Created Before: ").concat(createdBefore)
                .concat(" | Created After: ").concat(createdAfter));

        val result = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customer,
                new Date(tryParseLong(createdBefore, "createdBefore")),
                new Date(tryParseLong(createdAfter, "createdAfter")), pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        log.info("result".concat(result.toString()));

        return ResponseEntity.ok(mapPaged(result));
    }


    @RequestMapping(value = "/v1/transactions/save", method = RequestMethod.POST)
    private ResponseEntity<TransactionView> saveTransactionV1(@RequestBody Transaction transaction) {
        log.info("Calling: saveTransactionV1 >> ".concat(transaction.toString()));


        Customer customer = customerService.findById(transaction.getCustomerId());


        val saved = transactionService.save(Transaction
                .builder()
                .id(transaction.getId())
                .created(transaction.getCreated())
                .amount(transaction.getAmount())
                .customerId(transaction.getCustomerId())
                .customer(customer)
                .build());

        return ResponseEntity.ok(mapTransactionToTransactionView(saved));
    }

    @RequestMapping(value = "/v1/transactions/delete/{id}", method = RequestMethod.DELETE)
    private ResponseEntity<TransactionView> deleteTransactionV1(@PathVariable String id) {
        log.info("Calling: deleteCustomerV1 >> ".concat(id));

        val result = transactionService.hardDelete(tryParseInteger(id, "id"));

        return ResponseEntity.ok(mapTransactionToTransactionView(result));
    }


    private TransactionViewPagedData mapPaged(GenericPagedModel<Transaction> transactions) {
        return TransactionViewPagedData
                .builder()
                .totalElements(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .numberOfElements(transactions.getNumberOfElements())
                .content(mapTransactionsCollectiontoTransactionViewList(transactions.getContent()))
                .build();
    }

    private List<TransactionView> mapTransactionsCollectiontoTransactionViewList(Collection<Transaction> transactions) {
        return transactions.stream().map(this::mapTransactionToTransactionView).toList();
    }



    private TransactionView mapTransactionToTransactionView(Transaction transaction) {
        return TransactionView.builder()
                .amount(transaction.getAmount())
                .created(transaction.getCreated())
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .build();
    }

}
