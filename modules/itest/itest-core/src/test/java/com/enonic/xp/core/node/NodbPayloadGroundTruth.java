package com.enonic.xp.core.node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.enonic.xp.core.nodb.NodbTestCluster;

/**
 * Phase 3 Gate C (nodb/BUILD-PHASE-3.md): SQL ground-truth helpers against the tenant
 * schema's own {@code payload} table, independent of what NoDB itself reports over the
 * wire -- the same style of assertion {@link NodbTestCluster#s3Client()} enables for
 * binaries (Phase 2 Gate C), one layer lower for node payloads.
 * <p>
 * A tenant's Postgres schema name is exactly its tenant id (see
 * {@code com.enonic.nodb.engine.TenantContext}'s javadoc) -- no lookup/mapping needed,
 * only schema-qualification. {@code tenantId} always comes from a validated
 * {@code TenantContext} (a real provisioned tenant, {@code ^[a-z][a-z0-9]{2,30}$}), never
 * from unvalidated input, so building the schema-qualified identifier by simple
 * concatenation (rather than a bind parameter, which Postgres does not support for
 * identifiers) carries no injection risk here.
 */
final class NodbPayloadGroundTruth
{
    private NodbPayloadGroundTruth()
    {
    }

    /** {@code SELECT count(*) FROM "<tenantId>".payload WHERE hash = ?} -- expected 0 or 1 (hash is the table's primary key). */
    static long countPayloadRows( final String tenantId, final String hash )
    {
        final String sql = "SELECT count(*) FROM " + quoteIdent( tenantId ) + ".payload WHERE hash = ?";
        try (Connection connection = NodbTestCluster.get().dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement( sql ))
        {
            statement.setString( 1, hash );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
        catch ( SQLException e )
        {
            throw new IllegalStateException( "Ground-truth payload query failed for tenant [" + tenantId + "]", e );
        }
    }

    /** True iff a row with this exact hash exists in {@code tenantId}'s own {@code payload} table. */
    static boolean payloadExists( final String tenantId, final String hash )
    {
        return countPayloadRows( tenantId, hash ) > 0;
    }

    /** All three of {@code hashes} exist in {@code tenantId}'s own {@code payload} table. */
    static boolean allPayloadsExist( final String tenantId, final Set<String> hashes )
    {
        return hashes.stream().allMatch( hash -> payloadExists( tenantId, hash ) );
    }

    /** {@code SELECT count(*) FROM "<tenantId>".node_version WHERE <hashColumn> = ?} -- how many versions reference this payload hash. */
    static long countVersionsReferencingHash( final String tenantId, final String hashColumn, final String hash )
    {
        final String sql = "SELECT count(*) FROM " + quoteIdent( tenantId ) + ".node_version WHERE " + hashColumn + " = ?";
        try (Connection connection = NodbTestCluster.get().dataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement( sql ))
        {
            statement.setString( 1, hash );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
        catch ( SQLException e )
        {
            throw new IllegalStateException( "Ground-truth node_version query failed for tenant [" + tenantId + "]", e );
        }
    }

    private static String quoteIdent( final String identifier )
    {
        return "\"" + identifier.replace( "\"", "\"\"" ) + "\"";
    }
}
