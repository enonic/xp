package com.enonic.nodb.engine;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Executes work inside a single connection/transaction scoped to one tenant, per
 * DESIGN.md §7.2: pooled connections run as a low-privilege service role; on checkout
 * NoDB issues {@code SET LOCAL ROLE <tenant>} (plus {@code SET LOCAL search_path}), so a
 * wrong-schema query — bug, injection, or otherwise — fails with {@code permission
 * denied} in the database itself rather than leaking rows.
 */
public final class Tx
{
    private Tx()
    {
    }

    @FunctionalInterface
    public interface TenantWork<T>
    {
        T apply( Connection connection )
            throws SQLException;
    }

    public static <T> T inTenantTx( DataSource dataSource, TenantContext tenant, TenantWork<T> work )
        throws SQLException
    {
        String quotedTenant = Identifiers.quote( tenant.tenantId() );

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit( false );
            try
            {
                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "SET LOCAL ROLE " + quotedTenant );
                    statement.execute( "SET LOCAL search_path TO " + quotedTenant );
                }

                T result = work.apply( connection );
                connection.commit();
                return result;
            }
            catch ( SQLException | RuntimeException e )
            {
                safeRollback( connection );
                throw e;
            }
            finally
            {
                resetRoleAndAutoCommit( connection );
            }
        }
    }

    /**
     * Runs MULTIPLE reads in ONE repeatable-read, read-only transaction and returns the
     * composed result — the checked single-snapshot read facility required by
     * {@code nodb/FINDINGS.md} #1 and locked as Phase 5 decision 1
     * ({@code nodb/BUILD-PHASE-5.md}).
     *
     * <p><b>The invariant this exists to uphold:</b> every {@link #inTenantTx} call is its
     * own connection, transaction and snapshot, so two consecutive calls are two snapshots
     * — and a decision computed from read A must never be enforced by write B in a
     * different snapshot. That exact shape silently buried an outbox row below the
     * indexer's checkpoint (Phase 4 Gate C: {@code next} in one transaction, {@code
     * maxSeq} in another, a write committing in the window invisible to the first and
     * visible to the second). Any code that composes a decision from more than one read —
     * "is this payload referenced?", "which versions are older than the threshold?", a
     * mark phase of any kind — belongs in here, so all its reads observe one snapshot.
     *
     * <p><b>Writes are refused, loudly and by the database.</b> The connection is marked
     * read-only at the JDBC level AND the transaction itself is {@code SET TRANSACTION
     * ISOLATION LEVEL REPEATABLE READ, READ ONLY}, so an attempted write fails in Postgres
     * with SQLSTATE 25006 ("cannot execute ... in a read-only transaction") rather than
     * silently committing under a snapshot the caller believed was pure. Acting on what
     * was read happens after this returns — in one {@link #inTenantTx} whose write is
     * guarded against staleness (e.g. monotonic {@code GREATEST}, a keyed {@code DELETE}
     * of exactly the rows the snapshot returned), or behind an explicit persisted grace
     * window (decision 1's mark/sweep rule).
     *
     * <p>Tenant scoping is identical to {@link #inTenantTx}: {@code SET LOCAL ROLE} +
     * {@code SET LOCAL search_path}, so a wrong-schema read still fails with
     * {@code permission denied} in the database itself.
     */
    public static <T> T inTenantSnapshot( DataSource dataSource, TenantContext tenant, TenantWork<T> work )
        throws SQLException
    {
        String quotedTenant = Identifiers.quote( tenant.tenantId() );

        try (Connection connection = dataSource.getConnection())
        {
            connection.setReadOnly( true );
            connection.setAutoCommit( false );
            try
            {
                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "SET TRANSACTION ISOLATION LEVEL REPEATABLE READ, READ ONLY" );
                    statement.execute( "SET LOCAL ROLE " + quotedTenant );
                    statement.execute( "SET LOCAL search_path TO " + quotedTenant );
                }

                T result = work.apply( connection );
                connection.commit();
                return result;
            }
            catch ( SQLException | RuntimeException e )
            {
                safeRollback( connection );
                throw e;
            }
            finally
            {
                resetRoleAndAutoCommit( connection );
                try
                {
                    connection.setReadOnly( false );
                }
                catch ( SQLException ignore )
                {
                    // best-effort: connection may be in a broken state after a failed rollback
                }
            }
        }
    }

    /**
     * Like {@link #inTenantTx}, but does NOT issue {@code SET LOCAL ROLE <tenant>}: the
     * connection keeps the pooled service role's own privileges, only {@code search_path}
     * is scoped to the tenant schema. Tenant roles deliberately hold DML grants only
     * (SELECT/INSERT/UPDATE/DELETE) and never DDL rights (see {@link TenantProvisioner}),
     * so operations that must run DDL against a tenant schema at runtime — repo/branch
     * lifecycle partition {@code CREATE TABLE}/{@code DROP TABLE} — cannot use
     * {@link #inTenantTx}. This is the same trust model {@link TenantProvisioner} and
     * {@link MigrationRunner} already use for schema/template DDL; use it ONLY for that
     * class of trusted-internal, DDL-issuing operation, never for ordinary data-plane
     * reads/writes, which must stay behind the {@code SET LOCAL ROLE} boundary.
     */
    public static <T> T inTenantSchema( DataSource dataSource, TenantContext tenant, TenantWork<T> work )
        throws SQLException
    {
        String quotedTenant = Identifiers.quote( tenant.tenantId() );

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit( false );
            try
            {
                try (Statement statement = connection.createStatement())
                {
                    statement.execute( "SET LOCAL search_path TO " + quotedTenant );
                }

                T result = work.apply( connection );
                connection.commit();
                return result;
            }
            catch ( SQLException | RuntimeException e )
            {
                safeRollback( connection );
                throw e;
            }
            finally
            {
                try
                {
                    connection.setAutoCommit( true );
                }
                catch ( SQLException ignore )
                {
                    // best-effort: connection may be in a broken state after a failed rollback
                }
            }
        }
    }

    private static void safeRollback( Connection connection )
    {
        try
        {
            connection.rollback();
        }
        catch ( SQLException ignore )
        {
            // connection may already be broken; nothing more we can do
        }
    }

    private static void resetRoleAndAutoCommit( Connection connection )
    {
        // SET LOCAL is transaction-scoped and already reverted by the commit/rollback
        // above; RESET ROLE is issued explicitly regardless, so the connection never
        // returns to the pool under a stale role identity even if that assumption
        // changes later.
        try (Statement statement = connection.createStatement())
        {
            statement.execute( "RESET ROLE" );
        }
        catch ( SQLException ignore )
        {
            // best-effort: connection may be in a broken state after a failed rollback
        }
        try
        {
            connection.setAutoCommit( true );
        }
        catch ( SQLException ignore )
        {
            // best-effort, same reasoning as above
        }
    }
}
