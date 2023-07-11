package com.customer.transaction;

import com.customer.transaction.data.service.CustomerLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.customer.transaction.data.service.CustomerService;
import com.customer.transaction.data.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;


@SuppressWarnings("rawtypes")
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Ignore
public abstract class TestBase {
    private static final MySQLContainer container;
    private static final String IMAGE_VERSION = "mysql:8.0";

    @LocalServerPort
    public int port;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public CustomerService customerService;
    @Autowired
    public TransactionService transactionService;
    @Autowired
    public CustomerLogService customerLogService;

    @Autowired
    public ObjectMapper objectMapper;

    static {
        container = new MySQLContainer<>(IMAGE_VERSION)
                .withUsername("test_user")
                .withPassword("test_password")
                .withInitScript("ddl.sql")
                .withDatabaseName("bank");
        container.start();
    }

    @DynamicPropertySource
    public static void overrideContainerProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", container::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", container::getPassword);
    }
}
