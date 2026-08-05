package com.enonic.nodb.engine.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@code index_checkpoint} (migration 001, unchanged): "the indexer has applied everything up to
 * and including this outbox seq".
 *
 * <p>This single number IS the read-your-writes contract (DESIGN §3.3). A write returns the seq it
 * committed; {@code refresh(SEARCH)} blocks until the checkpoint reaches it.
 *
 * <p><b>Monotonic by SQL, not by discipline.</b> {@link #advance} writes
 * {@code GREATEST(existing, new)}, so the checkpoint can never move backwards regardless of who
 * calls it or in what order. That matters under interleaved writers: two indexer passes (or, later,
 * two indexer instances on one cell) can finish out of order, and a plain assignment would let the
 * slower one publish a checkpoint that is behind — which does not merely look untidy, it makes
 * {@code awaitRefresh} return for a seq whose documents are visible and then, on the next call,
 * block again for one that was already applied. The row's own updated_at is refreshed only when
 * the seq actually moves.
 */
public final class IndexCheckpointStore
{
    /** The single search indexer's checkpoint row. One row per consumer; there is one consumer. */
    public static final String SEARCH_INDEXER = "search";

    private IndexCheckpointStore()
    {
    }

    public static long read( Connection connection, String indexer )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "SELECT seq FROM index_checkpoint WHERE indexer = ?" ))
        {
            statement.setString( 1, indexer );
            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next() ? resultSet.getLong( 1 ) : 0L;
            }
        }
    }

    /** Returns the checkpoint value after the advance — i.e. {@code max(previous, seq)}. */
    public static long advance( Connection connection, String indexer, long seq )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO index_checkpoint (indexer, seq) VALUES (?, ?)
            ON CONFLICT (indexer) DO UPDATE
                SET seq = GREATEST(index_checkpoint.seq, EXCLUDED.seq),
                    updated_at = CASE WHEN EXCLUDED.seq > index_checkpoint.seq THEN now() ELSE index_checkpoint.updated_at END
            RETURNING seq
            """ ))
        {
            statement.setString( 1, indexer );
            statement.setLong( 2, seq );
            try (ResultSet resultSet = statement.executeQuery())
            {
                resultSet.next();
                return resultSet.getLong( 1 );
            }
        }
    }
}
