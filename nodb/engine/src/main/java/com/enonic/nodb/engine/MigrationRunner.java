package com.enonic.nodb.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Hand-rolled migration runner (no Flyway, per DESIGN.md §8: explicit wiring only).
 * Applies ordered SQL resources from {@code nodb/migrations/tenant/} to a tenant schema,
 * recording the applied template_version in {@code nodb_system.tenant}.
 *
 * <p>Runs on a caller-supplied {@link Connection} and does not manage the transaction
 * boundary itself: the caller (in practice {@link TenantProvisioner}) controls
 * commit/rollback, applying schema/role/grant DDL and the template as one transaction.
 * Each migration file is sent to Postgres as a single multi-statement string; if the
 * caller's connection is not already inside an open transaction, Postgres itself wraps
 * that string in an implicit transaction block, so a mid-script failure cannot leave the
 * template half-applied even when called standalone.
 */
final class MigrationRunner
{
    private static final String MIGRATIONS_PATH = "nodb/migrations/tenant/";

    private static final String MANIFEST_RESOURCE = MIGRATIONS_PATH + "manifest.txt";

    private MigrationRunner()
    {
    }

    static void migrateTenantSchema( Connection connection, String tenantSchema )
        throws SQLException
    {
        ensureSystemSchema( connection );

        List<String> resources = orderedMigrationResources();
        int currentVersion = currentTemplateVersion( connection, tenantSchema );

        for ( int i = currentVersion; i < resources.size(); i++ )
        {
            String resource = resources.get( i );
            int newVersion = i + 1;
            String sql = readResource( MIGRATIONS_PATH + resource );

            try (Statement statement = connection.createStatement())
            {
                statement.execute( "SET LOCAL search_path TO " + Identifiers.quote( tenantSchema ) );
                statement.execute( sql );
            }

            recordTemplateVersion( connection, tenantSchema, newVersion );
        }
    }

    static void ensureSystemSchema( Connection connection )
        throws SQLException
    {
        try (Statement statement = connection.createStatement())
        {
            statement.execute( "CREATE SCHEMA IF NOT EXISTS nodb_system" );
            statement.execute( """
                CREATE TABLE IF NOT EXISTS nodb_system.tenant (
                    tenant_id        text PRIMARY KEY,
                    template_version int NOT NULL,
                    created_at       timestamptz NOT NULL DEFAULT now()
                )
                """ );
        }
    }

    private static int currentTemplateVersion( Connection connection, String tenantSchema )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT template_version FROM nodb_system.tenant WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantSchema );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getInt( 1 ) : 0;
            }
        }
    }

    private static void recordTemplateVersion( Connection connection, String tenantSchema, int version )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO nodb_system.tenant (tenant_id, template_version)
            VALUES (?, ?)
            ON CONFLICT (tenant_id) DO UPDATE SET template_version = EXCLUDED.template_version
            """ ))
        {
            statement.setString( 1, tenantSchema );
            statement.setInt( 2, version );
            statement.executeUpdate();
        }
    }

    static List<String> orderedMigrationResources()
    {
        try (InputStream in = MigrationRunner.class.getClassLoader().getResourceAsStream( MANIFEST_RESOURCE ))
        {
            if ( in == null )
            {
                throw new IllegalStateException( "Missing migration manifest on classpath: " + MANIFEST_RESOURCE );
            }
            List<String> names = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader( new InputStreamReader( in, StandardCharsets.UTF_8 ) ))
            {
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    String trimmed = line.strip();
                    if ( !trimmed.isEmpty() && !trimmed.startsWith( "#" ) )
                    {
                        names.add( trimmed );
                    }
                }
            }
            return List.copyOf( names );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static String readResource( String path )
    {
        try (InputStream in = MigrationRunner.class.getClassLoader().getResourceAsStream( path ))
        {
            if ( in == null )
            {
                throw new IllegalStateException( "Missing migration resource on classpath: " + path );
            }
            return new String( in.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
