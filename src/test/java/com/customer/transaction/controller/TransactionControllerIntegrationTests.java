package com.customer.transaction.controller;
import com.customer.transaction.RestConfiguration;
import com.customer.transaction.TestBase;
import com.customer.transaction.controller.View.CustomerView;
import com.customer.transaction.controller.View.TransactionView;
import com.customer.transaction.controller.View.TransactionViewPagedData;
import com.customer.transaction.data.model.Customer;
import com.customer.transaction.data.model.Transaction;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.CustomerPagedData;
import com.customer.transaction.util.TransactionPagedData;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;


public class TransactionControllerIntegrationTests extends TestBase {
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
                .customerId(newCustomer1.getId())
                .customer(newCustomer1)
                .build());
    }

    public void insertNewTransaction2() {
        newTransaction2 = transactionService.save(Transaction
                .builder()
                .id(2)
                .amount(1000.0)
                .created(new Date(Instant.now().toEpochMilli()))
                .customerId(newCustomer2.getId())
                .customer(newCustomer2)
                .build());
    }

    public void testPagedDataResponse(TransactionPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());
        assertEquals(2, transaction.getTotalElements());
        assertEquals(1, transaction.getTotalPages());
        assertEquals(2, transaction.getNumberOfElements());

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> String.valueOf(t.getId()).equals(newTransaction1.getId().toString())));

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> String.valueOf(t.getId()).equals(newTransaction2.getId().toString())));
    }

    public void testPagedDataOfOne(TransactionViewPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomerId().equals(newCustomer1.getId())));
    }

    public void testPagedDataOfTwo(TransactionViewPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomerId().equals(newCustomer2.getId())));
    }


    @Before
    public void setup() {
        customerService.hardDeleteAll();
        transactionService.hardDeleteAll();
    }

    @Test
    public void insert_new_transaction_test() {
        insertNewCustomer1();
        val transactionToPost = TransactionView.builder()
                .id(1)
                .amount(50000.0)
                .created(new Date(Instant.now().toEpochMilli()))
                .customerId(1)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/save");

        val response = restTemplate.postForEntity(url, new HttpEntity<>(transactionToPost), TransactionView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        val found = transactionService.findById(response.getBody().getId());

        assertNotNull(found);
        assertEquals(String.valueOf(response.getBody().getId()), found.getId().toString());
        assertEquals(String.valueOf(response.getBody().getAmount()), found.getAmount().toString());
        assertEquals(String.valueOf(response.getBody().getCustomerId()), found.getCustomerId().toString());
    }


    @Test
    public void get_transaction_by_id_test() {
        insertNewCustomer1();
        insertNewTransaction1();
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/")
                .concat(newTransaction1.getId().toString());
        val response = restTemplate.getForEntity(url, TransactionView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        assertEquals(String.valueOf(newTransaction1.getId()), String.valueOf(response.getBody().getId().toString()));
        assertEquals(String.valueOf(newTransaction1.getCustomerId()), String.valueOf(response.getBody().getCustomerId().toString()));
        assertEquals(String.valueOf(newTransaction1.getAmount()), String.valueOf(response.getBody().getAmount().toString()));
    }

    @Test
    public void get_transaction_by_id_with_exception_test() {
        val id = 1;
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/")
                .concat(String.valueOf(id));
        try {
            restTemplate.getForEntity(url, TransactionView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString(String.valueOf(id)));
        }
    }

    @Test
    public void get_transactions_test() {
        insertNewCustomer1();
        insertNewCustomer2();
        insertNewTransaction1();
        insertNewTransaction2();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all");
        val response = restTemplate.getForEntity(url, TransactionPagedData.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        testPagedDataResponse(response.getBody());
    }

    @Test
    public void get_customers_with_exception_test() {
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all");
        try {
            restTemplate.getForEntity(url, TransactionPagedData.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
        }
    }

    @Test
    public void get_transactions_by_customer_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all_by_customer");

        val response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(newCustomer1), TransactionViewPagedData.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        testPagedDataOfOne(response.getBody());
    }

    @Test
    public void get_transactions_by_customer_and_created_before_and_created_after_test() {
        insertNewCustomer1();
        insertNewCustomer2();
        insertNewTransaction1();
        insertNewTransaction2();

        val yesterday = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
        val tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all_by_customer_created_before_created_after")
                .concat("?createdBefore=").concat(String.valueOf(tomorrow))
                .concat("&createdAfter=").concat(String.valueOf(yesterday));

        val response1 = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(newCustomer1), TransactionViewPagedData.class);
        val response2 = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(newCustomer2), TransactionViewPagedData.class);

        assertTrue(StringUtils.isNotBlank(response1.toString()));
        assertTrue(StringUtils.isNotBlank(response2.toString()));

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());

        testPagedDataOfOne(response1.getBody());
        testPagedDataOfTwo(response2.getBody());
    }


    @Test(expected = ResponseStatusException.class)
    public void delete_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();


        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/delete/")
                .concat(newTransaction1.getId().toString());

        val response = restTemplate.exchange(url, HttpMethod.DELETE, null, TransactionView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());
        assertEquals(String.valueOf(response.getBody().getId()), (newTransaction1.getId().toString()));

        try {
            transactionService.findById(newTransaction1.getId());
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString(String.valueOf(response.getBody().getId())));
        }
    }


}
