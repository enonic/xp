package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.MigrationRunner.Migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4 gate P3: immutable ordered migrations with recorded checksums, plus the pre-GA
 * adopt-on-first-run baseline rule. One Postgres container reused across all tests in this
 * class (same pattern as TenantProvisioningTest).
 */
@Testcontainers
class MigrationIntegrityTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static HikariDataSource dataSource;

    private static TenantProvisioner provisioner;

    private static List<Migration> migrations;

    @BeforeAll
    static void setUp()
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 4 );
        dataSource = new HikariDataSource( config );

        provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        migrations = MigrationRunner.loadMigrations();
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    @Test
    void freshProvisioningRecordsAChecksumForEveryOrderedMigration()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3fresh" );
        provisioner.provision( tenant );

        // Derived from the manifest rather than hardcoded, so adding a migration (Phase 4 Gate A
        // added 003) does not make this test a chore that gets weakened instead of updated.
        assertEquals( migrations.size(), templateVersion( "p3fresh" ) );
        assertEquals( migrations.size(), recordedVersions( "p3fresh" ).size() );

        for ( int version = 1; version <= migrations.size(); version++ )
        {
            assertEquals( migrations.get( version - 1 ).name(), recordedName( "p3fresh", version ) );
            assertEquals( migrations.get( version - 1 ).checksum(), recordedChecksum( "p3fresh", version ) );
        }
        assertEquals( "001_init.sql", recordedName( "p3fresh", 1 ) );
        assertEquals( "002_version_query_indexes.sql", recordedName( "p3fresh", 2 ) );
        assertEquals( "003_search_index.sql", recordedName( "p3fresh", 3 ) );
        assertEquals( "004_snapshot_gc.sql", recordedName( "p3fresh", 4 ) );
        assertTrue( recordedChecksum( "p3fresh", 1 ).startsWith( "sha256:" ) );
    }

    @Test
    void upgradeFromAPhase3ShapedTenantAppliesAndRecordsTheNewMigration()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3upgrade" );

        provisioner.provision( tenant, List.of( migrations.get( 0 ) ) );

        assertEquals( 1, templateVersion( "p3upgrade" ) );
        assertEquals( Set.of( 1 ), recordedVersions( "p3upgrade" ) );
        assertFalse( indexesIn( "p3upgrade" ).contains( "branch_entry_path_lower" ), "002 must not be applied yet" );
        assertFalse( indexesIn( "p3upgrade" ).contains( "search_document_replay" ), "003 must not be applied yet" );

        provisioner.provision( tenant );

        assertEquals( migrations.size(), templateVersion( "p3upgrade" ) );
        assertEquals( allVersions(), recordedVersions( "p3upgrade" ) );
        for ( int version = 1; version <= migrations.size(); version++ )
        {
            assertEquals( migrations.get( version - 1 ).checksum(), recordedChecksum( "p3upgrade", version ) );
        }
        assertTrue( indexesIn( "p3upgrade" ).contains( "branch_entry_path_lower" ), "002 must be applied by the upgrade run" );
        assertTrue( indexesIn( "p3upgrade" ).contains( "search_document_replay" ), "003 must be applied by the upgrade run" );
    }

    @Test
    void editingAnAlreadyAppliedMigrationIsRejectedAsImmutable()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3tamper" );
        provisioner.provision( tenant );

        MigrationIntegrityException thrown =
            assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, tamper002() ) );

        assertTrue( thrown.getMessage().contains( "002_version_query_indexes.sql has changed since it was applied to tenant p3tamper" ),
                    "expected the specific immutability error, got: " + thrown.getMessage() );
        assertTrue( thrown.getMessage().contains( "migrations are immutable" ),
                    "expected the specific immutability error, got: " + thrown.getMessage() );

        assertEquals( migrations.size(), templateVersion( "p3tamper" ) );
        assertEquals( migrations.get( 1 ).checksum(), recordedChecksum( "p3tamper", 2 ), "the recorded checksum must not be rewritten" );
        assertFalse( indexesIn( "p3tamper" ).contains( "p3_tampered_index" ), "the tampered DDL must not have run" );
    }

    @Test
    void whitespaceOnlyReformattingOfAnAppliedMigrationIsNotTampering()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3space" );
        provisioner.provision( tenant );

        Migration reformatted =
            new Migration( migrations.get( 1 ).name(), migrations.get( 1 ).sql().replace( "\n", "  \r\n" ) + "\r\n\r\n" );

        provisioner.provision( tenant, replacing( 1, reformatted ) );

        assertEquals( migrations.size(), templateVersion( "p3space" ) );
        assertEquals( migrations.get( 1 ).checksum(), recordedChecksum( "p3space", 2 ) );
    }

    @Test
    void renamingAnAlreadyAppliedMigrationIsRejected()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3rename" );
        provisioner.provision( tenant );

        List<Migration> renamed = replacing( 1, new Migration( "002_version_query_indices.sql", migrations.get( 1 ).sql() ) );

        MigrationIntegrityException thrown =
            assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, renamed ) );

        assertTrue( thrown.getMessage().contains( "was applied to tenant p3rename as '002_version_query_indexes.sql'" ),
                    "expected the rename error, got: " + thrown.getMessage() );
    }

    @Test
    void aTenantAheadOfTheManifestIsRejected()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3ahead" );
        provisioner.provision( tenant );

        MigrationIntegrityException thrown = assertThrows( MigrationIntegrityException.class,
                                                          () -> provisioner.provision( tenant, List.of( migrations.get( 0 ) ) ) );

        assertTrue( thrown.getMessage().contains( "forward-only" ), "expected the forward-only error, got: " + thrown.getMessage() );
    }

    @Test
    void anOutOfOrderManifestIsRejected()
    {
        TenantContext tenant = new TenantContext( "p3unordered" );
        List<Migration> swapped = List.of( migrations.get( 1 ), migrations.get( 0 ) );

        MigrationIntegrityException thrown =
            assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, swapped ) );

        assertTrue( thrown.getMessage().contains( "migration manifest is not ordered" ),
                    "expected the ordering error, got: " + thrown.getMessage() );
        assertTrue( thrown.getMessage().contains( "must be numbered 001" ), "expected the ordering error, got: " + thrown.getMessage() );
    }

    @Test
    void aGapInMigrationNumberingIsRejected()
    {
        TenantContext tenant = new TenantContext( "p3gap" );
        List<Migration> gapped = List.of( migrations.get( 0 ), new Migration( "004_later.sql", "SELECT 1" ) );

        MigrationIntegrityException thrown =
            assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, gapped ) );

        assertTrue( thrown.getMessage().contains( "must be numbered 002" ), "expected the gap error, got: " + thrown.getMessage() );
    }

    @Test
    void aManifestEntryThatIsNotANumberedMigrationFileIsRejected()
    {
        TenantContext tenant = new TenantContext( "p3badname" );
        List<Migration> named = List.of( new Migration( "init.sql", "SELECT 1" ) );

        MigrationIntegrityException thrown =
            assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, named ) );

        assertTrue( thrown.getMessage().contains( "not a NNN_name.sql migration file" ),
                    "expected the naming error, got: " + thrown.getMessage() );
    }

    @Test
    void aTenantWithNoRecordedChecksumsAdoptsTheCurrentFilesAsItsBaseline()
        throws SQLException
    {
        TenantContext tenant = new TenantContext( "p3adopt" );
        provisioner.provision( tenant );

        forgetRecordedChecksums( "p3adopt" );
        assertTrue( recordedVersions( "p3adopt" ).isEmpty(), "the pre-P3 tenant shape has a template_version but no checksums" );
        assertEquals( migrations.size(), templateVersion( "p3adopt" ) );

        provisioner.provision( tenant );

        assertEquals( allVersions(), recordedVersions( "p3adopt" ), "adoption must record a baseline instead of failing" );
        assertEquals( migrations.get( 0 ).checksum(), recordedChecksum( "p3adopt", 1 ) );
        assertEquals( migrations.get( 1 ).checksum(), recordedChecksum( "p3adopt", 2 ) );
        assertEquals( migrations.size(), templateVersion( "p3adopt" ) );

        // The adopted baseline is a real baseline: tampering is detected from now on.
        assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( tenant, tamper002() ) );
    }

    @Test
    void checksumStateIsPerTenantAndOneTenantsTamperingDoesNotAffectAnother()
        throws SQLException
    {
        TenantContext one = new TenantContext( "p3duala" );
        TenantContext two = new TenantContext( "p3dualb" );
        provisioner.provision( one );
        provisioner.provision( two );

        assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( one, tamper002() ) );

        provisioner.provision( two );

        assertEquals( migrations.get( 1 ).checksum(), recordedChecksum( "p3dualb", 2 ) );
        assertEquals( migrations.get( 1 ).checksum(), recordedChecksum( "p3duala", 2 ) );

        forgetRecordedChecksums( "p3duala" );
        provisioner.provision( one );

        assertEquals( allVersions(), recordedVersions( "p3duala" ) );
        assertEquals( allVersions(), recordedVersions( "p3dualb" ) );
        assertThrows( MigrationIntegrityException.class, () -> provisioner.provision( two, tamper002() ) );
    }

    /**
     * The full manifest with 002 edited. Must be the FULL list, not a truncated one: a short list
     * would trip the forward-only check first and the test would pass for the wrong reason.
     */
    private static List<Migration> tamper002()
    {
        return replacing( 1, new Migration( migrations.get( 1 ).name(), migrations.get( 1 ).sql() +
            "\nCREATE INDEX p3_tampered_index ON node_commit (commit_id);\n" ) );
    }

    /** The manifest with one slot swapped out — keeps these tests independent of how many migrations exist. */
    private static List<Migration> replacing( int index, Migration replacement )
    {
        List<Migration> modified = new java.util.ArrayList<>( migrations );
        modified.set( index, replacement );
        return List.copyOf( modified );
    }

    private static Set<Integer> allVersions()
    {
        Set<Integer> versions = new java.util.HashSet<>();
        for ( int version = 1; version <= migrations.size(); version++ )
        {
            versions.add( version );
        }
        return versions;
    }

    private static void forgetRecordedChecksums( String tenantId )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement( "DELETE FROM nodb_system.tenant_migration WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            statement.executeUpdate();
        }
    }

    private static Set<Integer> recordedVersions( String tenantId )
        throws SQLException
    {
        Set<Integer> versions = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement( "SELECT version FROM nodb_system.tenant_migration WHERE tenant_id = ?" ))
        {
            statement.setString( 1, tenantId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    versions.add( resultSet.getInt( 1 ) );
                }
            }
        }
        return versions;
    }

    private static String recordedName( String tenantId, int version )
        throws SQLException
    {
        return recordedColumn( tenantId, version, "name" );
    }

    private static String recordedChecksum( String tenantId, int version )
        throws SQLException
    {
        return recordedColumn( tenantId, version, "checksum" );
    }

    private static String recordedColumn( String tenantId, int version, String column )
        throws SQLException
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT " + column + " FROM nodb_system.tenant_migration WHERE tenant_id = ? AND version = ?" ))
        {
            statement.setString( 1, tenantId );
            statement.setInt( 2, version );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getString( 1 ) : null;
            }
        }
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
                return resultSet.next() ? resultSet.getInt( 1 ) : 0;
            }
        }
    }

    private static Set<String> indexesIn( String schema )
        throws SQLException
    {
        Set<String> indexes = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement( "SELECT indexname FROM pg_indexes WHERE schemaname = ?" ))
        {
            statement.setString( 1, schema );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    indexes.add( resultSet.getString( 1 ) );
                }
            }
        }
        return indexes;
    }
}
