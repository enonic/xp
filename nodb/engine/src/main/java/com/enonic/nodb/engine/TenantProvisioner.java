package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

/**
 * Provisions/deprovisions tenant schemas: schema + role + grants + template DDL
 * (DESIGN.md §4/§7.2). {@link #provision} is idempotent — calling it twice for the same
 * tenant is a no-op the second time (schema/role already exist, migrations already
 * applied, grants re-issued harmlessly).
 *
 * <p>Runs with the connecting {@link DataSource}'s own privileges: that "service role"
 * owns tenant schemas/tables. Tenant roles themselves only ever receive DML grants
 * (SELECT/INSERT/UPDATE/DELETE) plus schema USAGE — never DDL rights — and are granted
 * as a role membership to the service role so {@code SET LOCAL ROLE <tenant>} works
 * (see {@link Tx}).
 */
public final class TenantProvisioner
{
    private final DataSource dataSource;

    private final String serviceRole;

    public TenantProvisioner( DataSource dataSource, String serviceRole )
    {
        this.dataSource = dataSource;
        this.serviceRole = serviceRole;
    }

    public void provision( TenantContext tenant )
        throws SQLException
    {
        provision( tenant, MigrationRunner.loadMigrations() );
    }

    void provision( TenantContext tenant, List<MigrationRunner.Migration> migrations )
        throws SQLException
    {
        String schema = tenant.tenantId();
        String quotedSchema = Identifiers.quote( schema );

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit( false );
            try
            {
                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "CREATE SCHEMA IF NOT EXISTS " + quotedSchema );
                }

                createRoleIfMissing( connection, schema );

                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "GRANT USAGE ON SCHEMA " + quotedSchema + " TO " + quotedSchema );
                }

                MigrationRunner.migrateTenantSchema( connection, schema, migrations );

                try (Statement statement = connection.createStatement())
                {
                    // Table grants must follow the template DDL (ALL TABLES only covers
                    // tables that exist at the time the GRANT runs).
                    statement.execute(
                        "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA " + quotedSchema + " TO " + quotedSchema );
                    // Future runtime partitions (per-repo/per-branch DDL, created later)
                    // inherit the same grants automatically.
                    statement.execute( "ALTER DEFAULT PRIVILEGES IN SCHEMA " + quotedSchema +
                                            " GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO " + quotedSchema );
                    // So the pooled service connection can SET LOCAL ROLE <tenant>.
                    statement.execute( "GRANT " + quotedSchema + " TO " + Identifiers.quote( serviceRole ) );
                }

                connection.commit();
            }
            catch ( SQLException | RuntimeException e )
            {
                connection.rollback();
                throw e;
            }
            finally
            {
                connection.setAutoCommit( true );
            }
        }
    }

    public void dropTenant( TenantContext tenant )
        throws SQLException
    {
        String schema = tenant.tenantId();
        String quotedSchema = Identifiers.quote( schema );

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit( false );
            try
            {
                boolean roleExists = roleExists( connection, schema );
                if ( roleExists )
                {
                    try (Statement statement = connection.createStatement())
                    {
                        statement.execute( "REVOKE " + quotedSchema + " FROM " + Identifiers.quote( serviceRole ) );
                        statement.execute( "DROP OWNED BY " + quotedSchema );
                    }
                }

                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "DROP SCHEMA IF EXISTS " + quotedSchema + " CASCADE" );
                    if ( roleExists )
                    {
                        statement.execute( "DROP ROLE " + quotedSchema );
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM nodb_system.tenant WHERE tenant_id = ?" ))
                {
                    statement.setString( 1, schema );
                    statement.executeUpdate();
                }

                connection.commit();
            }
            catch ( SQLException | RuntimeException e )
            {
                connection.rollback();
                throw e;
            }
            finally
            {
                connection.setAutoCommit( true );
            }
        }
    }

    private void createRoleIfMissing( Connection connection, String roleName )
        throws SQLException
    {
        if ( !roleExists( connection, roleName ) )
        {
            try (Statement statement = connection.createStatement())
            {
                statement.execute( "CREATE ROLE " + Identifiers.quote( roleName ) + " NOLOGIN" );
            }
        }
    }

    private boolean roleExists( Connection connection, String roleName )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = ?" ))
        {
            statement.setString( 1, roleName );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next();
            }
        }
    }
}
