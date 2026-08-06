package com.enonic.nodb.engine.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The transactional outbox, from the indexer's side (the write side lives in
 * {@link com.enonic.nodb.engine.store.WriteService}).
 *
 * <p>DESIGN §3.3: every write commits an outbox row with a monotonic sequence IN THE SAME
 * TRANSACTION as the branch/version rows, so the search index can lag but can never miss a
 * committed write. The indexer applies rows in seq order and advances a checkpoint;
 * {@code refresh(SEARCH)} blocks until {@code checkpoint >= caller's last committed seq} and then
 * issues an OpenSearch refresh.
 *
 * <p><b>Delivery is at-least-once, and that is by design.</b> Applying a batch to OpenSearch and
 * advancing the checkpoint cannot be one atomic act across two systems, so the checkpoint is
 * advanced AFTER the bulk succeeds: a crash in between replays the batch. Every op is therefore
 * idempotent — bulk {@code index} is a full replace, and bulk {@code delete} of an absent document
 * reports {@code not_found} in the item rather than an error. The alternative ordering
 * (checkpoint first) would lose writes, which the contract forbids.
 */
public final class OutboxStore
{
    public static final String OP_INDEX = "INDEX";

    public static final String OP_DELETE = "DELETE";

    public static final String OP_DELETE_BRANCH = "DELETE_BRANCH";

    public static final String OP_DELETE_REPO = "DELETE_REPO";

    public static final String OP_REINDEX = "REINDEX";

    private OutboxStore()
    {
    }

    /**
     * The next batch strictly after {@code afterSeq}, in seq order — EVERY row, readable or not.
     *
     * <p>Joined to {@code repository} so the indexer gets the external repo id (index names are
     * built from it) without a second query per row. The join is a <b>LEFT</b> join, and that is a
     * correctness requirement rather than a style choice. {@code outbox} deliberately has no FK to
     * {@code repository} (it must survive a repo drop long enough to carry the DELETE_REPO row), so
     * a row whose repository is gone is an expected state; under an INNER join such a row was
     * INVISIBLE here, which meant the indexer's checkpoint would stall behind it forever, which in
     * turn forced {@link #maxSeq}-based "jump past whatever I cannot see" logic in the indexer —
     * and that logic could not distinguish an orphan from a row that had merely committed a
     * microsecond too late, so it silently dropped writes (see {@code Indexer#applyNextBatch}).
     *
     * <p>With a LEFT join every row is returned, {@code repoId} is {@code null} for an orphan, and
     * the indexer skips it explicitly. The checkpoint therefore only ever advances over rows this
     * query actually returned — which is precisely the invariant the at-least-once delivery
     * argument depends on.
     */
    public static List<OutboxEntry> next( Connection connection, long afterSeq, int limit )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            SELECT o.seq, o.repo_key, r.repo_id, o.branch, o.node_id, o.version_id, o.op
            FROM outbox o
            LEFT JOIN repository r ON r.repo_key = o.repo_key
            WHERE o.seq > ?
            ORDER BY o.seq
            LIMIT ?
            """ ))
        {
            statement.setLong( 1, afterSeq );
            statement.setInt( 2, limit );
            List<OutboxEntry> entries = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    entries.add( new OutboxEntry( resultSet.getLong( 1 ), resultSet.getLong( 2 ), resultSet.getString( 3 ),
                                                  resultSet.getString( 4 ), resultSet.getString( 5 ), resultSet.getString( 6 ),
                                                  resultSet.getString( 7 ) ) );
                }
            }
            return List.copyOf( entries );
        }
    }

    /**
     * The highest seq in the outbox, regardless of repo — an observation about the queue, useful to
     * tests and to lag reporting.
     *
     * <p><b>Never use this to advance the checkpoint.</b> It once served exactly that purpose (to
     * let the indexer step over rows {@link #next}'s inner join hid) and it was a lost-write bug:
     * {@code next} and this method run in two separate transactions, so a write committing in the
     * window between them is seen HERE but not THERE, and advancing the checkpoint to this value
     * silently buries it below the watermark forever. {@link #next} now returns orphan rows itself,
     * so nothing needs to guess what it could not see.
     */
    public static long maxSeq( Connection connection )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT coalesce(max(seq), 0) FROM outbox" );
             ResultSet resultSet = statement.executeQuery())
        {
            resultSet.next();
            return resultSet.getLong( 1 );
        }
    }

    /** One INDEX row per node id — the shape every write path funnels into. */
    public static Long appendIndex( Connection connection, long repoKey, String branch, Collection<String> nodeIds, String versionId )
        throws SQLException
    {
        return append( connection, repoKey, branch, nodeIds, versionId, OP_INDEX );
    }

    public static Long appendDelete( Connection connection, long repoKey, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        return append( connection, repoKey, branch, nodeIds, null, OP_DELETE );
    }

    /**
     * A node-less row: DELETE_BRANCH / DELETE_REPO / REINDEX are fan-out instructions the indexer
     * expands itself, not per-node changes.
     */
    public static Long appendControl( Connection connection, long repoKey, String branch, String op )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO outbox (repo_key, branch, op) VALUES (?, ?, ?) RETURNING seq" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, op );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
    }

    private static Long append( Connection connection, long repoKey, String branch, Collection<String> nodeIds, String versionId,
                                String op )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return null;
        }
        Long maxSeq = null;
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO outbox (repo_key, branch, node_id, version_id, op) VALUES (?, ?, ?, ?, ?) RETURNING seq" ))
        {
            for ( String nodeId : nodeIds )
            {
                statement.setLong( 1, repoKey );
                statement.setString( 2, branch );
                statement.setString( 3, nodeId );
                statement.setString( 4, versionId );
                statement.setString( 5, op );
                try (ResultSet resultSet = statement.executeQuery())
                {
                    resultSet.next();
                    long seq = resultSet.getLong( 1 );
                    if ( maxSeq == null || seq > maxSeq )
                    {
                        maxSeq = seq;
                    }
                }
            }
        }
        return maxSeq;
    }

    public record OutboxEntry(long seq, long repoKey, String repoId, String branch, String nodeId, String versionId, String op)
    {
    }
}
