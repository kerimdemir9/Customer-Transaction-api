package com.customer.transaction.data;

import com.customer.transaction.data.model.Customer;
import com.customer.transaction.TestBase;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.Assert.*;

public class CustomerServiceIntegrationTests extends TestBase{

    private static Customer newCustomer1;

    private static Customer newCustomer2;

    public void insertNewCustomer1() {
        newCustomer1 = customerService.save(Customer
                .builder()
                .fullName("test1_full_name")
                .phoneNumber("11111111111")
                .balance(30000.0)
                .build());
    }

    public void insertNewCustomer2() {
        newCustomer2 = customerService.save(Customer
                .builder()
                .fullName("test2_full_name")
                .phoneNumber("22222222222")
                .balance(60000.0)
                .build());
    }

    public void testCollection(GenericPagedModel<Customer> customer) {
        assertFalse(customer.getContent().isEmpty());

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> c.getId().equals(newCustomer1.getId())));

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> c.getId().equals(newCustomer2.getId())));
    }

    @Before
    public void setup() {
        customerService.hardDeleteAll();
    }

    @Test
    public void insert_customer_test() {
        insertNewCustomer1();
        assertNotNull(newCustomer1);
    }

    @Test
    public void update_customer_test() {
        insertNewCustomer1();

        val updated = customerService.save(Customer
                .builder()
                .id(newCustomer1.getId())
                .fullName("new full name")
                .phoneNumber("55555555555")
                .balance(20.0)
                .build());

        assertEquals(newCustomer1.getId(), updated.getId());
        assertNotEquals(newCustomer1.getFullName(), updated.getFullName());
        assertNotEquals(newCustomer1.getPhoneNumber(), updated.getPhoneNumber());
        assertNotEquals(newCustomer1.getBalance(), updated.getBalance());
    }

    @Test
    public void find_customer_by_id_test() {
        insertNewCustomer1();

        val found = customerService.findById(newCustomer1.getId());

        assertNotNull(found);
        assertEquals(newCustomer1.getId(), found.getId());
        assertEquals(newCustomer1.getFullName(), found.getFullName());
        assertEquals(newCustomer1.getPhoneNumber(), found.getPhoneNumber());
        assertEquals(newCustomer1.getBalance(), found.getBalance());
    }

    @Test
    public void find_all_customers_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        testCollection(customerService
                .findAll(0, 10, "id", SortDirection.Descending));
    }

    public void testNames(GenericPagedModel<Customer> customer, int testNum) {
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
    public void find_all_customers_by_full_name_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        testNames(customerService.findAllByFullName("test1_full_name", 0, 10, "id",
                        SortDirection.Descending), 1);
        testNames(customerService.findAllByFullName("test2_full_name", 0, 10, "id",
                SortDirection.Descending), 2);
    }


    @Test
    public void find_all_customers_by_by_balance_between_test() {
        insertNewCustomer2();
        insertNewCustomer1();

        val min = 10000.0;
        val max = 100000.0;

        testCollection(customerService
                .findAllCustomersByBalanceBetween(min, max,
                        0, 10, "id", SortDirection.Descending));
    }

    @Test(expected = ResponseStatusException.class)
    public void delete_costumer_test() {
        insertNewCustomer1();

        val deleted = customerService.hardDelete(newCustomer1.getId());

        assertEquals(newCustomer1.getId(), deleted.getId());

        customerService.findById(deleted.getId());
    }
}