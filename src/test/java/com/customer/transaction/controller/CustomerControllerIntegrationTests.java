package com.customer.transaction.controller;

import com.customer.transaction.RestConfiguration;
import com.customer.transaction.TestBase;
import com.customer.transaction.controller.View.CustomerView;
import com.customer.transaction.controller.View.CustomerViewPagedData;
import com.customer.transaction.data.model.Customer;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


public class CustomerControllerIntegrationTests extends TestBase {
    private static Customer newCustomer1;
    private static Customer newCustomer2;

    public void insertNewCustomer1() {
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

    public void testPagedDataResponse(CustomerViewPagedData customer) {
        assertFalse(customer.getContent().isEmpty());
        assertEquals(2, customer.getTotalElements());
        assertEquals(1, customer.getTotalPages());
        assertEquals(2, customer.getNumberOfElements());

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> String.valueOf(c.getId()).equals(newCustomer1.getId().toString())));

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> String.valueOf(c.getId()).equals(newCustomer2.getId().toString())));
    }

    @Before
    public void setup() {
        customerService.hardDeleteAll();
    }

    @Test
    public void get_customer_by_id_test() {
        insertNewCustomer1();
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/")
                .concat(newCustomer1.getId().toString());
        val response = restTemplate.getForEntity(url, CustomerView.class);
        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        assertEquals(String.valueOf(newCustomer1.getId()), String.valueOf(response.getBody().getId()));
        assertEquals(newCustomer1.getFullName(), response.getBody().getFullName());
        assertEquals(newCustomer1.getPhoneNumber(), response.getBody().getPhoneNumber());
    }

    @Test
    public void get_customer_by_id_with_exception_test() {
        val id = 1;
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/")
                .concat(String.valueOf(id));
        try {
            restTemplate.getForEntity(url, CustomerView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString(String.valueOf(id)));
        }
    }

    @Test
    public void get_customers_by_balance_between_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        val min = 20000.0;
        val max = 70000.0;

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all_by_balance_between/")
                .concat(String.valueOf(min)).concat("&").concat(String.valueOf(max));
        val response = restTemplate.getForEntity(url, CustomerViewPagedData.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        testPagedDataResponse(response.getBody());
    }


    public void testNames(CustomerViewPagedData customer, int testNum) {
        assertFalse(customer.getContent().isEmpty());

        if(testNum == 1) {
            assertTrue(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer1.getId())));
            assertFalse(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer2.getId())));
        }
        else {
            assertFalse(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer1.getId())));
            assertTrue(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer2.getId())));
        }
    }

    @Test
    public void get_customers_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all");
        val response = restTemplate.getForEntity(url, CustomerViewPagedData.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        testPagedDataResponse(response.getBody());
    }

    @Test
    public void get_customers_with_exception_test() {
        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all");
        try {
            restTemplate.getForEntity(url, CustomerViewPagedData.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
        }
    }

    @Test
    public void get_customers_by_full_name_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all_by_full_name/")
                .concat("test1_full_name");
        val response = restTemplate.getForEntity(url, CustomerViewPagedData.class);
        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        testNames(response.getBody(), 1);

        val url2 = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all_by_full_name/")
                .concat("test2_full_name");
        val response2 = restTemplate.getForEntity(url2, CustomerViewPagedData.class);
        assertTrue(StringUtils.isNotBlank(response2.toString()));
        assertNotNull(response2.getBody());


        testNames(response2.getBody(), 2);
    }

    @Test
    public void get_customers_by_full_name_with_exception_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/find_all_by_full_name/")
                .concat("test3_full_name");
        try {
            restTemplate.getForEntity(url, CustomerViewPagedData.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString("test3_full_name"));
        }
    }

    @Test
    public void delete_customers_test() {
        insertNewCustomer1();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/delete/")
                .concat(newCustomer1.getId().toString());

        val response = restTemplate.exchange(url, HttpMethod.DELETE, null, CustomerView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());
        assertEquals(String.valueOf(response.getBody().getId()), (newCustomer1.getId().toString()));

        try {
            customerService.findById(newCustomer1.getId());
        } catch (final ResponseStatusException ex) {
            assertThat(ex.getMessage(), containsString("404"));
            assertThat(ex.getMessage(), containsString(String.valueOf(response.getBody().getId())));
        }
    }

    @Test
    public void insert_customer_test() {
        val customerToPost = CustomerView.builder()
                .fullName("insertTest")
                .phoneNumber("33333333333")
                .balance(1000.0)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/save");

        val response = restTemplate.postForEntity(url,
                new HttpEntity<>(customerToPost), CustomerView.class);

        assertTrue(StringUtils.isNotBlank(response.toString()));
        assertNotNull(response.getBody());

        val found = customerService.findById(response.getBody().getId());

        assertNotNull(found);
        assertEquals(String.valueOf(response.getBody().getId()), found.getId().toString());
        assertEquals(response.getBody().getFullName(), found.getFullName());
        assertEquals(response.getBody().getPhoneNumber(), found.getPhoneNumber());
    }


    @Test
    public void insert_customer_with_exception_test1() {
        val customerToPost = CustomerView.builder()
                .fullName("    ")
                .phoneNumber("33333333333")
                .balance(1000.0)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/save");

        try {
            restTemplate.postForEntity(url,
                    new HttpEntity<>(customerToPost), CustomerView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must not be empty"));
        }
    }

    @Test
    public void insert_customer_with_exception_test2() {
        val customerToPost = CustomerView.builder()
                .fullName("test_valid_2")
                .phoneNumber("   ")
                .balance(1000.0)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/save");

        try {
            restTemplate.postForEntity(url,
                    new HttpEntity<>(customerToPost), CustomerView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must not be empty"));
        }
    }

    @Test
    public void insert_customer_with_exception_test3() {
        val customerToPost = CustomerView.builder()
                .fullName("test_valid3")
                .phoneNumber("33333333333")
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/save");

        try {
            restTemplate.postForEntity(url,
                    new HttpEntity<>(customerToPost), CustomerView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must not be null"));
        }
    }

    @Test
    public void insert_customer_with_exception_test4() {
        val customerToPost = CustomerView.builder()
                .fullName("test_valid3")
                .phoneNumber("33333333333")
                .balance(-1000.0)
                .build();

        val url = RestConfiguration.LOCALHOST
                .concat(String.valueOf(port))
                .concat("/v1/customers/save");


        try {
            restTemplate.postForEntity(url,
                    new HttpEntity<>(customerToPost), CustomerView.class);
        } catch (final HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("400"));
            assertThat(ex.getMessage(), containsString("must be greater than 0.0"));
        }
    }
}
