package com.enonic.nodb.engine;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.store.PayloadStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Tx#inTenantSnapshot}: the checked single-snapshot read facility of Phase 5 gate P1
 * (FINDINGS #1). Two properties are load-bearing and each gets a direct test: writes are refused
 * BY THE DATABASE (not by convention), and a commit landing between two reads of the same
 * callback is invisible to the second read — the exact window that buried an outbox row below
 * the indexer's checkpoint in Phase 4 Gate C.
 */
@Testcontainers
class TxSnapshotTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static HikariDataSource dataSource;

    private static final TenantContext ACME = new TenantContext( "acme" );

    private static final TenantContext FISK = new TenantContext( "fisk" );

    @BeforeAll
    static void setUp()
        throws SQLException
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );

        TenantProvisioner provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( ACME );
        provisioner.provision( FISK );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    /**
     * A write inside a snapshot must fail loudly in Postgres itself (SQLSTATE 25006), not commit
     * silently under a transaction the caller believed was pure reads.
     */
    @Test
    void aWriteInsideASnapshotFailsLoudlyInTheDatabase()
    {
        byte[] bytes = ( "forbidden-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );

        SQLException refused = assertThrows( SQLException.class, () -> Tx.inTenantSnapshot( dataSource, ACME,
                                                                                            connection -> PayloadStore.putPayload(
                                                                                                connection, bytes ) ) );

        assertEquals( "25006", refused.getSQLState(), "the refusal must be the database's read_only_sql_transaction error" );
        assertTrue( refused.getMessage().contains( "read-only" ),
                    "the database error must be surfaced, got: " + refused.getMessage() );
    }

    /**
     * Repeatable read, proven: a write committing between two reads of the same callback is
     * invisible to the second read. Under {@code inTenantTx}'s READ COMMITTED default the second
     * count sees the concurrent row and this test fails — which is exactly the two-snapshot
     * torn-read FINDINGS #1 describes, provoked here inside one callback instead of between two.
     */
    @Test
    void aCommitLandingMidCallbackIsInvisibleToTheSecondRead()
        throws SQLException
    {
        byte[] bytes = ( "mid-snapshot-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );

        long[] counts = Tx.inTenantSnapshot( dataSource, ACME, connection -> {
            long first = countPayloads( connection );
            try
            {
                Tx.inTenantTx( dataSource, ACME, inner -> PayloadStore.putPayload( inner, bytes ) );
            }
            catch ( SQLException e )
            {
                throw new IllegalStateException( "the concurrent commit is the provocation; it must succeed", e );
            }
            long second = countPayloads( connection );
            return new long[]{first, second};
        } );

        assertEquals( counts[0], counts[1], "a commit landing mid-snapshot leaked into the second read — that is not one snapshot" );

        long after = Tx.inTenantTx( dataSource, ACME, TxSnapshotTest::countPayloads );
        assertEquals( counts[0] + 1, after, "once the snapshot ends, the concurrent commit must be visible" );
    }

    /** The snapshot returns the callback's composed result — the whole point of composing reads. */
    @Test
    void composedReadsComeBackAsOneResult()
        throws SQLException
    {
        byte[] bytes = ( "composed-" + UUID.randomUUID() ).getBytes( StandardCharsets.UTF_8 );
        String hash = Tx.inTenantTx( dataSource, ACME, connection -> PayloadStore.putPayload( connection, bytes ) );

        Object[] composed = Tx.inTenantSnapshot( dataSource, ACME, connection -> new Object[]{
            PayloadStore.getPayload( connection, hash ), countPayloads( connection )} );

        assertNotNull( composed[0], "first read of the composed pair" );
        assertTrue( (Long) composed[1] >= 1, "second read of the composed pair" );
    }

    /** Read-only must not loosen tenant scoping: a wrong-schema read still dies in the database. */
    @Test
    void theSnapshotStillRunsUnderTheTenantRole()
    {
        SQLException denied = assertThrows( SQLException.class, () -> Tx.inTenantSnapshot( dataSource, ACME, connection -> {
            try (PreparedStatement statement = connection.prepareStatement( "SELECT count(*) FROM fisk.payload" );
                 ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        } ) );
        assertEquals( "42501", denied.getSQLState(), "cross-tenant reads must fail with permission denied, got: " + denied.getMessage() );
    }

    private static long countPayloads( java.sql.Connection connection )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT count(*) FROM payload" );
             ResultSet resultSet = statement.executeQuery())
        {
            resultSet.next();
            return resultSet.getLong( 1 );
        }
    }
}
