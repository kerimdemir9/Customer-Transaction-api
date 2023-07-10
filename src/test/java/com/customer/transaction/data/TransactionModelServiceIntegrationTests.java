package com.customer.transaction.data;

import com.customer.transaction.TestBase;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.model.TransactionModel;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class TransactionModelServiceIntegrationTests extends TestBase {
    private static TransactionModel newTransaction1Model;
    private static TransactionModel newTransaction2Model;

    private static CustomerModel newCustomer1Model;
    private static CustomerModel newCustomer2Model;

    private void insertNewCustomer1() {
        newCustomer1Model = customerService.save(CustomerModel
                .builder()
                .fullName("test1_full_name")
                .phoneNumber("11111111111")
                .balance(30000.0)
                .build());
    }

    public void insertNewCustomer2() {
        newCustomer2Model = customerService.save(CustomerModel
                .builder()
                .fullName("test2_full_name")
                .phoneNumber("22222222222")
                .balance(60000.0)
                .build());
    }


    public void insertNewTransaction1() {
        newTransaction1Model = transactionService.save(TransactionModel
                .builder()
                .amount(5000.0)
                .customer(newCustomer1Model)
                .build());
    }

    public void insertNewTransaction2() {
        newTransaction2Model = transactionService.save(TransactionModel
                .builder()
                .amount(5000.0)
                .customer(newCustomer2Model)
                .build());
    }

    public void testCollection(GenericPagedModel<TransactionModel> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> t.getId().equals(newTransaction1Model.getId())));

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> t.getId().equals(newTransaction2Model.getId())));
    }

    public void testCollectionOfOne(GenericPagedModel<TransactionModel> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomer().getId().equals(newCustomer1Model.getId())));
    }

    public void testCollectionOfTwo(GenericPagedModel<TransactionModel> transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomer().getId().equals(newCustomer2Model.getId())));
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
        assertNotNull(newTransaction1Model);
    }

    @Test
    public void update_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val updated = transactionService.save(TransactionModel
                .builder()
                .id(newTransaction1Model.getId())
                .customer(newCustomer1Model)
                .amount(800.0)
                .build());

        assertEquals(newTransaction1Model.getId(), updated.getId());
        assertNotEquals(newTransaction1Model.getAmount(), updated.getAmount());
    }

    @Test
    public void find_transaction_by_id_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val found = transactionService.findById(newTransaction1Model.getId());

        assertNotNull(found);
        assertEquals(newTransaction1Model.getId(), found.getId());
        assertEquals(newTransaction1Model.getAmount(), found.getAmount());
        assertEquals(newTransaction1Model.getCustomer().getId(), found.getCustomer().getId());
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

        val result1 = transactionService.findAllByCustomer(newCustomer1Model,0, 10, "id", SortDirection.Descending);
        val result2 = transactionService.findAllByCustomer(newCustomer2Model,0, 10, "id", SortDirection.Descending);


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

        val res1 = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(newCustomer1Model, yesterday, tomorrow , 0, 10, "id", SortDirection.Descending);
        val res2 = transactionService.findAllByCustomerAndCreatedBeforeAndCreatedAfter(newCustomer2Model, yesterday, tomorrow , 0, 10, "id", SortDirection.Descending);

        testCollectionOfOne(res1);
        testCollectionOfTwo(res2);
    }

    @Test(expected = ResponseStatusException.class)
    public void delete_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val deleted = transactionService.hardDelete(newTransaction1Model.getId());

        assertEquals(newTransaction1Model.getId(), deleted.getId());

        transactionService.findById(deleted.getId());
    }


}
