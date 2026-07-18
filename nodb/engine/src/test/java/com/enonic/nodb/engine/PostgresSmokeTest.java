package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 1: proves the testcontainers-postgres wiring works end to end before any
 * engine code is built on top of it.
 */
@Testcontainers
class PostgresSmokeTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    @Test
    void selectOneAgainstRealPostgres()
        throws Exception
    {
        assertTrue( POSTGRES.isRunning() );

        try (Connection connection = java.sql.DriverManager.getConnection( POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
                                                                            POSTGRES.getPassword() );
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery( "SELECT 1" ))
        {
            assertTrue( resultSet.next() );
            assertEquals( 1, resultSet.getInt( 1 ) );
        }
    }
}
