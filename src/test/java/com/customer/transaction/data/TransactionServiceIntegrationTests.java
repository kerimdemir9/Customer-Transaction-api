package com.customer.transaction.data;

import com.customer.transaction.TestBase;
import com.customer.transaction.data.model.Transaction;
import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class TransactionServiceIntegrationTests extends TestBase {
    private static Transaction newTransaction1;
    private static Transaction newTransaction2;

    private static Customer newCustomer1;
    private static Customer newCustomer2;

    private void insertNewCustomer1() {
        newCustomer1 = customerService.save(Customer
                .builder()
                .id(1)
                .fullName("test1_full_name")
                .phoneNumber("11111111111")
                .balance(30000.0)
                .build());
    }

    public void insertNewCustomer2() {
        newCustomer2 = customerService.save(Customer
                .builder()
                .id(2)
                .fullName("test2_full_name")
                .phoneNumber("22222222222")
                .balance(60000.0)
                .build());
    }


    public void insertNewTransaction1() {
        newTransaction1 = transactionService.save(Transaction
                .builder()
                .id(1)
                .amount(5000.0)
                .created(new Date(Instant.now().toEpochMilli()))
                .customer(newCustomer1)
                .build());
    }

    public void insertNewTransaction2() {
        newTransaction2 = transactionService.save(Transaction
                .builder()
                .id(1)
                .amount(5000.0)
                .created(new Date(Instant.now().toEpochMilli()))
                .customer(newCustomer2)
                .build());
    }

    public void testCollection(GenericPagedModel<Transaction> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> t.getId().equals(newTransaction1.getId())));

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> t.getId().equals(newTransaction2.getId())));
    }

    public void testCollectionOfOne(GenericPagedModel<Transaction> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomer().getId().equals(newCustomer1.getId())));
    }

    public void testCollectionOfTwo(GenericPagedModel<Transaction> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomer().getId().equals(newCustomer2.getId())));
    }

    @Before
    public void setup() {
        customerService.hardDeleteAll();
        transactionService.hardDeleteAll();
    }

    @Test
    public void insert_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();
        assertNotNull(newTransaction1);
    }

    @Test
    public void update_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val updated = transactionService.save(Transaction
                .builder()
                .id(newTransaction1.getId())
                .customer(newCustomer1)
                .amount(800.0)
                .build());

        assertEquals(newTransaction1.getId(), updated.getId());
        assertNotEquals(newTransaction1.getAmount(), updated.getAmount());
    }

    @Test
    public void find_transaction_by_id_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val found = transactionService.findById(newTransaction1.getId());

        assertNotNull(found);
        assertEquals(newTransaction1.getId(), found.getId());
        assertEquals(newTransaction1.getAmount(), found.getAmount());
        assertEquals(newTransaction1.getCustomerId(), found.getCustomerId());
    }

    @Test
    public void find_all_transactions_test() {
        insertNewCustomer1();
        insertNewCustomer2();
        insertNewTransaction1();
        insertNewTransaction2();

        testCollection(transactionService.findAll(0,10,"id", SortDirection.Descending));
    }


    @Test
    public void find_all_by_customer_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        insertNewTransaction1();
        insertNewTransaction2();

        val result1 = transactionService.findAllByCustomer(newCustomer1,0, 10, "id", SortDirection.Descending);
        val result2 = transactionService.findAllByCustomer(newCustomer2,0, 10, "id", SortDirection.Descending);


        assertNotNull(result1);
        assertNotNull(result2);

        testCollectionOfOne(result1);
        testCollectionOfTwo(result2);
    }


    @Test
    public void find_all_by_customer_created_before_and_created_after_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        insertNewTransaction1();
        insertNewTransaction2();

        val yesterday = new Date(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
        val tomorrow = new Date(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());

        val res1 = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(newCustomer1, yesterday, tomorrow , 0, 10, "id", SortDirection.Descending);
        val res2 = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(newCustomer2, yesterday, tomorrow , 0, 10, "id", SortDirection.Descending);

        testCollectionOfOne(res1);
        testCollectionOfTwo(res2);
    }

    @Test(expected = ResponseStatusException.class)
    public void delete_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val deleted = transactionService.hardDelete(newTransaction1.getId());

        assertEquals(newTransaction1.getId(), deleted.getId());

        transactionService.findById(deleted.getId());
    }


}
