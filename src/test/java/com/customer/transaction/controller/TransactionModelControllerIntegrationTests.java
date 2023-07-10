package com.customer.transaction.controller;
import com.customer.transaction.RestConfiguration;
import com.customer.transaction.TestBase;
import com.customer.transaction.controller.View.TransactionView;
import com.customer.transaction.controller.View.TransactionViewPagedData;
import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.data.model.TransactionModel;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;


public class TransactionModelControllerIntegrationTests extends TestBase {
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
                .created(new Date(Instant.now().toEpochMilli()))
                .customer(newCustomer1Model)
                .build());
    }

    public void insertNewTransaction2() {
        newTransaction2Model = transactionService.save(TransactionModel
                .builder()
                .amount(1000.0)
                .created(new Date(Instant.now().toEpochMilli()))
                .customer(newCustomer2Model)
                .build());
    }

    public void testPagedDataResponse(TransactionViewPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());
        assertEquals(2, transaction.getTotalElements());
        assertEquals(1, transaction.getTotalPages());
        assertEquals(2, transaction.getNumberOfElements());

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> String.valueOf(t.getId()).equals(newTransaction1Model.getId().toString())));

        assertTrue(transaction.getContent()
                .stream()
                .anyMatch(t -> String.valueOf(t.getId()).equals(newTransaction2Model.getId().toString())));
    }

    public void testPagedDataOfOne(TransactionViewPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomerId().equals(newCustomer1Model.getId())));
    }

    public void testPagedDataOfTwo(TransactionViewPagedData transaction) {
        assertFalse(transaction.getContent().isEmpty());

        assertTrue(transaction.getContent()
                .stream()
                .allMatch(t -> t.getCustomerId().equals(newCustomer2Model.getId())));
    }


    @Before
    public void setup() {
        customerService.hardDeleteAll();
        transactionService.hardDeleteAll();
    }

    @Test
    public void get_transaction_by_id_test() {
        insertNewCustomer1();
        insertNewTransaction1();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/")
                .concat(newTransaction1Model.getId().toString());

        val response = restTemplate.getForEntity(url, TransactionView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        assertEquals(String.valueOf(newTransaction1Model.getId()), String.valueOf(response.getBody().getId()));
        assertEquals(String.valueOf(newTransaction1Model.getCustomer().getId()), String.valueOf(response.getBody().getCustomerId()));
        assertEquals(String.valueOf(newTransaction1Model.getAmount()), String.valueOf(response.getBody().getAmount()));
    }

    @Test
    public void get_transaction_by_id_with_exception_test() {
        val id = RandomUtils.nextInt(1,10);

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

        val response = restTemplate.getForEntity(url, TransactionViewPagedData.class);

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
            restTemplate.getForEntity(url, TransactionViewPagedData.class);
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
                .concat("/v1/transactions/find_all_by_customer/")
                .concat(newCustomer1Model.getId().toString());

        val response = restTemplate.getForEntity(url, TransactionViewPagedData.class);
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

        val url1 = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all_by_customer_created_before_created_after/")
                .concat(newCustomer1Model.getId().toString())
                .concat("?createdBefore=").concat(String.valueOf(yesterday))
                .concat("&createdAfter=").concat(String.valueOf(tomorrow));

        val url2 = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/find_all_by_customer_created_before_created_after/")
                .concat(newCustomer2Model.getId().toString())
                .concat("?createdBefore=").concat(String.valueOf(yesterday))
                .concat("&createdAfter=").concat(String.valueOf(tomorrow));

        val response1 = restTemplate.getForEntity(url1, TransactionViewPagedData.class);
        val response2 = restTemplate.getForEntity(url2, TransactionViewPagedData.class);

        assertTrue(StringUtils.isNotBlank(response1.toString()));
        assertTrue(StringUtils.isNotBlank(response2.toString()));

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());

        testPagedDataOfOne(response1.getBody());
        testPagedDataOfTwo(response2.getBody());
    }

    @Test
    public void insert_new_transaction_test() {
        insertNewCustomer1();

        val transactionToPost = TransactionView.builder()
                .amount(50000.0)
                .customerId(newCustomer1Model.getId())
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
        assertEquals(String.valueOf(response.getBody().getCustomerId()), found.getCustomer().getId().toString());
    }


    @Test
    public void insert_new_transaction_with_exception_test1() {
        insertNewCustomer1();

        val transactionToPost = TransactionView.builder()
                .amount(50000.0)
                .customerId(null)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/save");

        try { // restTemplate => HttpClientError
            restTemplate.postForEntity(url, new HttpEntity<>(transactionToPost), TransactionView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("406"));
            assertThat(ex.getMessage(), containsString("customerId must not be null"));
        }
    }

    @Test
    public void insert_new_transaction_with_exception_test2() {
        insertNewCustomer1();

        val transactionToPost = TransactionView.builder()
                .amount(null)
                .customerId(newCustomer1Model.getId())
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/save");

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(transactionToPost), TransactionView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must not be null"));
        }
    }

    @Test
    public void insert_new_transaction_with_exception_test3() {
        insertNewCustomer1();

        val transactionToPost = TransactionView.builder()
                .amount(-1000.0)
                .customerId(newCustomer1Model.getId())
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/save");

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(transactionToPost), TransactionView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must be greater than 0.0"));
        }
    }

    @Test
    public void delete_transaction_test() {
        insertNewCustomer1();
        insertNewTransaction1();


        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/transactions/delete/")
                .concat(newTransaction1Model.getId().toString());

        val response = restTemplate.exchange(url, HttpMethod.DELETE, null, TransactionView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());
        assertEquals(String.valueOf(response.getBody().getId()), (newTransaction1Model.getId().toString()));

        try { // Using service => ResponseStatusException
            transactionService.findById(newTransaction1Model.getId());
        } catch (final ResponseStatusException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString(String.valueOf(response.getBody().getId())));
        }
    }


}
