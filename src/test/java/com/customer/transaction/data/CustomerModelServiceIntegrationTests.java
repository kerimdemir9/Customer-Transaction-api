package com.customer.transaction.data;

import com.customer.transaction.data.model.CustomerModel;
import com.customer.transaction.TestBase;
import com.customer.transaction.data.util.GenericPagedModel;
import com.customer.transaction.util.SortDirection;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.Assert.*;

public class CustomerModelServiceIntegrationTests extends TestBase{

    private static CustomerModel newCustomer1Model;

    private static CustomerModel newCustomer2Model;

    public void insertNewCustomer1() {
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

    public void testCollection(GenericPagedModel<CustomerModel> customer) {
        assertFalse(customer.getContent().isEmpty());

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> c.getId().equals(newCustomer1Model.getId())));

        assertTrue(customer.getContent()
                .stream()
                .anyMatch(c -> c.getId().equals(newCustomer2Model.getId())));
    }

    @Before
    public void setup() {
        customerService.hardDeleteAll();
    }

    @Test
    public void insert_customer_test() {
        insertNewCustomer1();
        assertNotNull(newCustomer1Model);
    }

    @Test
    public void update_customer_test() {
        insertNewCustomer1();

        val updated = customerService.save(CustomerModel
                .builder()
                .id(newCustomer1Model.getId())
                .fullName("new full name")
                .phoneNumber("55555555555")
                .balance(20.0)
                .build());

        assertEquals(newCustomer1Model.getId(), updated.getId());
        assertNotEquals(newCustomer1Model.getFullName(), updated.getFullName());
        assertNotEquals(newCustomer1Model.getPhoneNumber(), updated.getPhoneNumber());
        assertNotEquals(newCustomer1Model.getBalance(), updated.getBalance());
    }

    @Test
    public void find_customer_by_id_test() {
        insertNewCustomer1();

        val found = customerService.findById(newCustomer1Model.getId());

        assertNotNull(found);
        assertEquals(newCustomer1Model.getId(), found.getId());
        assertEquals(newCustomer1Model.getFullName(), found.getFullName());
        assertEquals(newCustomer1Model.getPhoneNumber(), found.getPhoneNumber());
        assertEquals(newCustomer1Model.getBalance(), found.getBalance());
    }

    @Test
    public void find_all_customers_test() {
        insertNewCustomer1();
        insertNewCustomer2();

        testCollection(customerService
                .findAll(0, 10, "id", SortDirection.Descending));
    }

    public void testNames(GenericPagedModel<CustomerModel> customer, int testNum) {
        assertFalse(customer.getContent().isEmpty());

        if(testNum == 1) {
            assertTrue(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer1Model.getId())));
            assertFalse(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer2Model.getId())));
        }
        else {
            assertFalse(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer1Model.getId())));
            assertTrue(customer.getContent()
                    .stream()
                    .anyMatch(c -> c.getId().equals(newCustomer2Model.getId())));
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

        val deleted = customerService.hardDelete(newCustomer1Model.getId());

        assertEquals(newCustomer1Model.getId(), deleted.getId());

        customerService.findById(deleted.getId());
    }
}