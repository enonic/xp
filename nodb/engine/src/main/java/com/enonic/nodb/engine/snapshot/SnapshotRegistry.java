package com.enonic.nodb.engine.snapshot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code snapshot} registry rows (migration 004). Static Connection-scoped methods,
 * same convention as the other stores; every method runs on a connection already inside a
 * {@code Tx} helper (role + search_path set).
 *
 * <p><b>State machine:</b> {@code CREATING} → {@code COMPLETE} | {@code FAILED}.
 * {@link #insertCreating} runs BEFORE any object is uploaded, so a create that dies at any
 * point leaves an identifiable {@code CREATING} (or, if the failure was caught in-process,
 * {@code FAILED}) row next to its orphan prefix — never a prefix that has to be
 * reverse-engineered into a snapshot. Only {@link #markComplete} — which runs strictly
 * after every artifact is durably on the object store — makes a row trustworthy; nothing
 * ever trusts a non-COMPLETE row (restore will refuse them, GC will treat their hashes as
 * unreferenced once they age out of {@code gc_grace} — Gates B/C).
 */
public final class SnapshotRegistry
{
    public static final String STATE_CREATING = "CREATING";

    public static final String STATE_COMPLETE = "COMPLETE";

    public static final String STATE_FAILED = "FAILED";

    private static final String COLUMNS =
        "snapshot_id, scope, repo_id, repo_key, created_at, expires_at, outbox_seq, state, location, format_version, "
            + "version_count, head_count, commit_count, document_count, hash_count, total_bytes, manifest_sha256";

    private SnapshotRegistry()
    {
    }

    /**
     * Inserts the {@code CREATING} row, stamping {@code expires_at} from the tenant's
     * {@code retention_policy.snapshot_horizon} IN THE SAME STATEMENT — the policy read and
     * the expiry it produces cannot be torn apart by a concurrent policy change (decision 2:
     * expiry is fixed at creation time; a later policy change never moves it, in either
     * direction). {@code outbox_seq} is 0 here: the real value is captured as the snapshot
     * transaction's first statement and stamped by {@link #markComplete}.
     */
    public static void insertCreating( Connection connection, String snapshotId, String scope, String repoId, Long repoKey,
                                       String location )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO snapshot (snapshot_id, scope, repo_id, repo_key, expires_at, outbox_seq, state, location)
            SELECT ?, ?, ?, ?, now() + snapshot_horizon, 0, 'CREATING', ? FROM retention_policy
            """ ))
        {
            statement.setString( 1, snapshotId );
            statement.setString( 2, scope );
            statement.setString( 3, repoId );
            if ( repoKey == null )
            {
                statement.setNull( 4, java.sql.Types.BIGINT );
            }
            else
            {
                statement.setLong( 4, repoKey );
            }
            statement.setString( 5, location );
            if ( statement.executeUpdate() != 1 )
            {
                throw new IllegalStateException( "retention_policy singleton row is missing; tenant schema is corrupt" );
            }
        }
    }

    /**
     * Flips {@code CREATING} → {@code COMPLETE} and stamps the verification metadata. Guarded
     * on {@code state = 'CREATING'} so a row that was concurrently deleted or failed is never
     * silently resurrected as trustworthy.
     */
    public static void markComplete( Connection connection, String snapshotId, long outboxSeq, long versionCount, long headCount,
                                     long commitCount, long documentCount, long hashCount, long totalBytes, String manifestSha256 )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            UPDATE snapshot
            SET state = 'COMPLETE', outbox_seq = ?, version_count = ?, head_count = ?, commit_count = ?,
                document_count = ?, hash_count = ?, total_bytes = ?, manifest_sha256 = ?
            WHERE snapshot_id = ? AND state = 'CREATING'
            """ ))
        {
            statement.setLong( 1, outboxSeq );
            statement.setLong( 2, versionCount );
            statement.setLong( 3, headCount );
            statement.setLong( 4, commitCount );
            statement.setLong( 5, documentCount );
            statement.setLong( 6, hashCount );
            statement.setLong( 7, totalBytes );
            statement.setString( 8, manifestSha256 );
            statement.setString( 9, snapshotId );
            if ( statement.executeUpdate() != 1 )
            {
                throw new IllegalStateException(
                    "snapshot " + snapshotId + " is no longer in CREATING state; refusing to mark it COMPLETE" );
            }
        }
    }

    /** Best-effort {@code CREATING} → {@code FAILED}; a no-op if the row is gone or already terminal. */
    public static void markFailed( Connection connection, String snapshotId )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "UPDATE snapshot SET state = 'FAILED' WHERE snapshot_id = ? AND state = 'CREATING'" ))
        {
            statement.setString( 1, snapshotId );
            statement.executeUpdate();
        }
    }

    public static SnapshotRecord get( Connection connection, String snapshotId )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT " + COLUMNS + " FROM snapshot WHERE snapshot_id = ?" ))
        {
            statement.setString( 1, snapshotId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    /** Every registry row of this tenant, newest first. */
    public static List<SnapshotRecord> list( Connection connection )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "SELECT " + COLUMNS + " FROM snapshot ORDER BY created_at DESC, snapshot_id" );
             ResultSet resultSet = statement.executeQuery())
        {
            List<SnapshotRecord> records = new ArrayList<>();
            while ( resultSet.next() )
            {
                records.add( map( resultSet ) );
            }
            return List.copyOf( records );
        }
    }

    /** Deletes the row; returns it, or {@code null} when no row existed (plain DELETE semantics). */
    public static SnapshotRecord delete( Connection connection, String snapshotId )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "DELETE FROM snapshot WHERE snapshot_id = ? RETURNING " + COLUMNS ))
        {
            statement.setString( 1, snapshotId );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? map( resultSet ) : null;
            }
        }
    }

    private static SnapshotRecord map( ResultSet resultSet )
        throws SQLException
    {
        long repoKey = resultSet.getLong( "repo_key" );
        boolean repoKeyNull = resultSet.wasNull();
        return new SnapshotRecord( resultSet.getString( "snapshot_id" ), resultSet.getString( "scope" ),
                                   resultSet.getString( "repo_id" ), repoKeyNull ? null : repoKey,
                                   resultSet.getTimestamp( "created_at" ).toInstant(), resultSet.getTimestamp( "expires_at" ).toInstant(),
                                   resultSet.getLong( "outbox_seq" ), resultSet.getString( "state" ), resultSet.getString( "location" ),
                                   resultSet.getInt( "format_version" ), nullableLong( resultSet, "version_count" ),
                                   nullableLong( resultSet, "head_count" ), nullableLong( resultSet, "commit_count" ),
                                   nullableLong( resultSet, "document_count" ), nullableLong( resultSet, "hash_count" ),
                                   nullableLong( resultSet, "total_bytes" ), resultSet.getString( "manifest_sha256" ) );
    }

    private static Long nullableLong( ResultSet resultSet, String column )
        throws SQLException
    {
        long value = resultSet.getLong( column );
        return resultSet.wasNull() ? null : value;
    }
}
