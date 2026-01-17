package com.dam.pms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        assertNotNull(dataSource, "Datasource should not be null");

        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertTrue(conn.isValid(1), "Connection should be valid");
        }
    }
}
