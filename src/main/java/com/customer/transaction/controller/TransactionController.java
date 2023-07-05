package com.customer.transaction.controller;

import com.customer.transaction.controller.View.CustomerView;
import com.customer.transaction.controller.View.PagedData;
import com.customer.transaction.controller.View.TransactionView;
import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.model.Transaction;
import com.customer.transaction.data.service.TransactionService;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.customer.transaction.controller.util.Parsers.tryParseInteger;
import static com.customer.transaction.controller.util.Parsers.tryParseLong;

@RestController
@Slf4j
public class TransactionController {

    final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "/v1/transactions/{id}", method = RequestMethod.GET)
    private ResponseEntity<TransactionView> getTransactionByIdV1(@PathVariable String id) {
        log.info("Calling: getTransactionByIdV1 >> ".concat(id));

        val result = transactionService.findById(tryParseInteger(id, "id"));

        return ResponseEntity.ok(mapTransactionToTransactionView(result));
    }

    @RequestMapping(value = "/v1/transactions/find_all", method = RequestMethod.GET)
    private ResponseEntity<PagedData<TransactionView>> getAllTransactionsV1(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllTransactionsV1");

        val result = transactionService.findAll(pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        return ResponseEntity.ok(mapPaged(result));
    }

    @RequestMapping(value = "/v1/transactions/find_all_by_customer", method = RequestMethod.GET)
    private ResponseEntity<PagedData<TransactionView>> getAllTransactionsByCustomerV1(
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
    private ResponseEntity<PagedData<TransactionView>> getAllByCustomerAndCreatedBeforeAndCreatedAfterV1(
            @RequestBody Customer customer,
            @RequestParam(defaultValue = "2023-07-05 23:00:52") String createdBefore,
            @RequestParam(defaultValue = "2023-07-05 00:00:52") String createdAfter,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Calling: getAllByCustomerAndCreatedBeforeAndCreatedAfterV1 >> Customer fullName: "
                .concat(customer.getFullName())
                .concat(" | Created Before: ").concat(createdBefore)
                .concat(" | Created After: ").concat(createdAfter));

        val result = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(customer,
                new Date(tryParseLong(createdBefore, "createdBefore")),
                new Date(tryParseLong(createdAfter, "createdAfter")), pageNo, pageSize, sortBy, SortDirection.of(sortDir));

        log.info("result".concat(result.toString()));

        return ResponseEntity.ok(mapPaged(result));
    }


    private PagedData<TransactionView> mapPaged(GenericPagedModel<Transaction> transactions) {
        return PagedData.<TransactionView>builder()
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
                .customer_id(transaction.getCustomer().getId())
                .build();
    }

}
