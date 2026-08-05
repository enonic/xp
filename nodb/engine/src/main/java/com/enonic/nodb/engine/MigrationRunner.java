package com.enonic.nodb.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hand-rolled migration runner (no Flyway, per DESIGN.md §8: explicit wiring only).
 * Applies ordered SQL resources from {@code nodb/migrations/tenant/} to a tenant schema,
 * recording the applied template_version in {@code nodb_system.tenant} and one checksum
 * row per applied migration in {@code nodb_system.tenant_migration}.
 *
 * <p>Runs on a caller-supplied {@link Connection} and does not manage the transaction
 * boundary itself: the caller (in practice {@link TenantProvisioner}) controls
 * commit/rollback, applying schema/role/grant DDL and the template as one transaction.
 * Each migration file is sent to Postgres as a single multi-statement string; if the
 * caller's connection is not already inside an open transaction, Postgres itself wraps
 * that string in an implicit transaction block, so a mid-script failure cannot leave the
 * template half-applied even when called standalone.
 *
 * <p><b>Migrations are immutable and forward-only</b> (BUILD-PHASE-4.md gate P3). Three
 * invariants are enforced on every provisioning/upgrade run, each failing with a
 * {@link MigrationIntegrityException} rather than a downstream SQL error:
 * <ol>
 * <li>the manifest is ordered, gapless and numbered from 001 — entry <i>n</i> must be
 *     named {@code <n as 3 digits>_<name>.sql};</li>
 * <li>a slot already applied to the tenant still carries the same file name;</li>
 * <li>a slot already applied to the tenant still has the same checksum — editing an
 *     applied migration is rejected; the fix is always a new migration file.</li>
 * </ol>
 *
 * <p><b>Checksum and normalization.</b> {@code sha256:<hex>} (the same shape as the
 * content-addressed {@code payload.hash} keys) over the file's UTF-8 content after
 * normalizing line endings ({@code \r\n} and {@code \r} to {@code \n}) and stripping
 * trailing whitespace from every line plus the end of the file. Rationale: those are
 * exactly the edits tooling makes without a human intending a change — {@code
 * core.autocrlf} on a fresh checkout, an editor's strip-trailing-whitespace-on-save, a
 * missing or added final newline — and a checksum that fires on them would cry wolf
 * instead of catching real drift. Every other difference, down to one character of SQL or
 * one word of a comment, changes the checksum. The one thing a migration may therefore
 * not rely on is significant trailing whitespace inside a multi-line string literal.
 */
final class MigrationRunner
{
    private static final Logger LOG = LoggerFactory.getLogger( MigrationRunner.class );

    private static final String MIGRATIONS_PATH = "nodb/migrations/tenant/";

    private static final String MANIFEST_RESOURCE = MIGRATIONS_PATH + "manifest.txt";

    private static final Pattern MIGRATION_NAME = Pattern.compile( "(\\d{3})_[a-z0-9_]+\\.sql" );

    private MigrationRunner()
    {
    }

    record Migration(String name, String sql)
    {
        String checksum()
        {
            return MigrationRunner.checksum( sql );
        }
    }

    static void migrateTenantSchema( Connection connection, String tenantSchema )
        throws SQLException
    {
        migrateTenantSchema( connection, tenantSchema, loadMigrations() );
    }

    static void migrateTenantSchema( Connection connection, String tenantSchema, List<Migration> migrations )
        throws SQLException
    {
        verifyManifestOrder( migrations );

        ensureSystemSchema( connection );

        int currentVersion = currentTemplateVersion( connection, tenantSchema );
        if ( currentVersion > migrations.size() )
        {
            throw new MigrationIntegrityException(
                "tenant " + tenantSchema + " has template_version " + currentVersion + " but only " + migrations.size() +
                    " migrations exist on the classpath; migrations are forward-only — a tenant is never downgraded" );
        }

        verifyAppliedMigrations( connection, tenantSchema, migrations, currentVersion );

        for ( int i = currentVersion; i < migrations.size(); i++ )
        {
            Migration migration = migrations.get( i );
            int newVersion = i + 1;

            try (Statement statement = connection.createStatement())
            {
                statement.execute( "SET LOCAL search_path TO " + Identifiers.quote( tenantSchema ) );
                statement.execute( migration.sql() );
            }

            recordTemplateVersion( connection, tenantSchema, newVersion );
            recordMigration( connection, tenantSchema, newVersion, migration );
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
            statement.execute( """
                CREATE TABLE IF NOT EXISTS nodb_system.tenant_migration (
                    tenant_id  text NOT NULL REFERENCES nodb_system.tenant (tenant_id) ON DELETE CASCADE,
                    version    int NOT NULL,
                    name       text NOT NULL,
                    checksum   text NOT NULL,
                    applied_at timestamptz NOT NULL DEFAULT now(),
                    PRIMARY KEY (tenant_id, version)
                )
                """ );
        }
    }

    private static void verifyManifestOrder( List<Migration> migrations )
    {
        for ( int i = 0; i < migrations.size(); i++ )
        {
            String name = migrations.get( i ).name();
            Matcher matcher = MIGRATION_NAME.matcher( name );
            if ( !matcher.matches() )
            {
                throw new MigrationIntegrityException(
                    "migration manifest entry " + ( i + 1 ) + " is '" + name + "', which is not a NNN_name.sql migration file" );
            }
            int expected = i + 1;
            int declared = Integer.parseInt( matcher.group( 1 ) );
            if ( declared != expected )
            {
                throw new MigrationIntegrityException(
                    "migration manifest is not ordered: entry " + expected + " is '" + name + "' but must be numbered " +
                        String.format( "%03d", expected ) + "; the manifest must be ordered and gapless from 001" );
            }
        }
    }

    /**
     * Verifies the tenant's already-applied migrations against the files on disk.
     *
     * <p><b>The pre-GA adopt-on-first-run rule (BUILD-PHASE-4.md gate P3).</b> Tenants
     * provisioned before this gate recorded a template_version but no checksums, so an
     * applied slot with no {@code tenant_migration} row is an UNKNOWN state, not a
     * mismatch. On the first run after this gate such a tenant ADOPTS the current file
     * checksums as its baseline (one log line per adopted slot) and proceeds; only slots
     * that do have a recorded checksum are compared. This is safe strictly because
     * everything is pre-GA: the protocol and schema are a draft, every installation is a
     * development installation whose tenants are provisioned from scratch by
     * itest/bench/dev-stack runs, so a slot with no checksum can only have come from the
     * matching file in this same working tree.
     *
     * <p><b>At GA this rule must become an error.</b> No adoption: a tenant whose applied
     * migrations have no recorded checksums is in an unknown state, and with durable
     * customer data "assume it matches" is exactly the assumption that hides a divergent
     * schema. The GA behaviour is to fail like a tamper does and require an explicit,
     * audited operator action (a recorded baseline import) to resolve it.
     */
    private static void verifyAppliedMigrations( Connection connection, String tenantSchema, List<Migration> migrations,
                                                int currentVersion )
        throws SQLException
    {
        if ( currentVersion == 0 )
        {
            return;
        }

        Map<Integer, String[]> recorded = recordedMigrations( connection, tenantSchema );

        for ( int version = 1; version <= currentVersion; version++ )
        {
            Migration migration = migrations.get( version - 1 );
            String[] row = recorded.get( version );

            if ( row == null )
            {
                LOG.info( "Adopting migration {} (version {}) as the recorded baseline for tenant {}: applied before checksums " +
                              "existed (pre-GA adopt-on-first-run rule)", migration.name(), version, tenantSchema );
                recordMigration( connection, tenantSchema, version, migration );
                continue;
            }

            String recordedName = row[0];
            String recordedChecksum = row[1];

            if ( !recordedName.equals( migration.name() ) )
            {
                throw new MigrationIntegrityException( "migration version " + version + " was applied to tenant " + tenantSchema +
                                                           " as '" + recordedName + "' but the manifest now lists '" + migration.name() +
                                                           "'; migrations are immutable — add a new migration instead" );
            }

            if ( !recordedChecksum.equals( migration.checksum() ) )
            {
                throw new MigrationIntegrityException(
                    "migration " + migration.name() + " has changed since it was applied to tenant " + tenantSchema +
                        "; migrations are immutable — add a new migration instead (recorded " + recordedChecksum + ", found " +
                        migration.checksum() + ")" );
            }
        }
    }

    private static Map<Integer, String[]> recordedMigrations( Connection connection, String tenantSchema )
        throws SQLException
    {
        Map<Integer, String[]> recorded = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT version, name, checksum FROM nodb_system.tenant_migration WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantSchema );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    recorded.put( resultSet.getInt( 1 ), new String[]{resultSet.getString( 2 ), resultSet.getString( 3 )} );
                }
            }
        }
        return recorded;
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

    private static void recordMigration( Connection connection, String tenantSchema, int version, Migration migration )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO nodb_system.tenant_migration (tenant_id, version, name, checksum)
            VALUES (?, ?, ?, ?)
            """ ))
        {
            statement.setString( 1, tenantSchema );
            statement.setInt( 2, version );
            statement.setString( 3, migration.name() );
            statement.setString( 4, migration.checksum() );
            statement.executeUpdate();
        }
    }

    static List<Migration> loadMigrations()
    {
        return orderedMigrationResources().stream()
            .map( name -> new Migration( name, readResource( MIGRATIONS_PATH + name ) ) )
            .toList();
    }

    static String checksum( String sql )
    {
        String normalized = Arrays.stream( sql.split( "\r\n|\r|\n", -1 ) )
            .map( String::stripTrailing )
            .collect( Collectors.joining( "\n" ) )
            .stripTrailing();
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            return "sha256:" + HexFormat.of().formatHex( digest.digest( normalized.getBytes( StandardCharsets.UTF_8 ) ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
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
