package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 2 tests #1-4 (test #5, TenantContext id validation, lives in TenantContextTest).
 * One Postgres container reused across all tests in this class.
 */
@Testcontainers
class TenantProvisioningTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static final Set<String> TEMPLATE_TABLES =
        Set.of( "repository", "branch", "payload", "node_version", "node_commit", "branch_entry", "outbox", "index_checkpoint",
                "audit_log" );

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    @BeforeAll
    static void setUp()
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 4 );
        dataSource = new HikariDataSource( config );

        // In production this is a low-privilege pooled service role (DESIGN.md §7.2);
        // here it's simply the container's connecting user.
        provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    @Test
    void provisioningCreatesAllTemplateTablesForEachTenant()
        throws SQLException
    {
        provisioner.provision( new TenantContext( "acme" ) );
        provisioner.provision( new TenantContext( "fisk" ) );

        assertEquals( TEMPLATE_TABLES, tablesIn( "acme" ) );
        assertEquals( TEMPLATE_TABLES, tablesIn( "fisk" ) );
    }

    @Test
    void crossSchemaAccessAsTenantRoleFailsWithPermissionDenied()
        throws SQLException
    {
        provisioner.provision( new TenantContext( "acme" ) );
        provisioner.provision( new TenantContext( "fisk" ) );

        TenantContext acme = new TenantContext( "acme" );

        SQLException thrown = assertThrows( SQLException.class, () -> Tx.inTenantTx( dataSource, acme, connection -> {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery( "SELECT * FROM fisk.repository" ))
            {
                return resultSet.next();
            }
        } ) );

        assertTrue( thrown.getMessage().toLowerCase().contains( "permission denied" ),
                    "expected a permission-denied SQLException, got: " + thrown.getMessage() );
    }

    @Test
    void ownSchemaAccessSucceedsUnderTenantRole()
        throws SQLException
    {
        provisioner.provision( new TenantContext( "acme" ) );
        TenantContext acme = new TenantContext( "acme" );

        long countAfterInsert = Tx.inTenantTx( dataSource, acme, connection -> {
            try (PreparedStatement insert = connection.prepareStatement( "INSERT INTO repository (repo_id) VALUES (?)" ))
            {
                insert.setString( 1, "myrepo-" + System.nanoTime() );
                insert.executeUpdate();
            }
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery( "SELECT count(*) FROM repository" ))
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        } );

        assertTrue( countAfterInsert >= 1 );
    }

    @Test
    void provisioningTwiceIsIdempotent()
        throws SQLException
    {
        TenantContext acme = new TenantContext( "acme" );

        provisioner.provision( acme );
        Set<String> tablesAfterFirst = tablesIn( "acme" );
        int templateVersionAfterFirst = templateVersion( "acme" );

        provisioner.provision( acme );

        assertEquals( tablesAfterFirst, tablesIn( "acme" ) );
        assertEquals( templateVersionAfterFirst, templateVersion( "acme" ) );
    }

    private static Set<String> tablesIn( String schema )
        throws SQLException
    {
        Set<String> tables = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT table_name FROM information_schema.tables WHERE table_schema = ?" ))
        {
            statement.setString( 1, schema );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    tables.add( resultSet.getString( 1 ) );
                }
            }
        }
        return tables;
    }

    private static int templateVersion( String tenantId )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT template_version FROM nodb_system.tenant WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getInt( 1 );
            }
        }
    }
}
