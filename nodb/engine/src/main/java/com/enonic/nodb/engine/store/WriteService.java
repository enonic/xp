package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.enonic.nodb.engine.model.BranchEntryRecord;

/**
 * The transactional WriteBatch (DESIGN.md §10 risk #1, the core deliverable of this
 * slice): N versions + N branch entries + an optional commit + inline/hash-only payloads,
 * committed as ONE transaction with ONE outbox row per branch-entry change.
 *
 * <p><b>NEED_PAYLOAD approach:</b> the work order describes this as "abort (rollback via
 * thrown exception)". This implementation instead validates every {@link PayloadRef.HashOnly}
 * reference BEFORE any row is written — versions/branch-entries/commit/outbox inserts only
 * start after every hash-only reference is confirmed present. That ordering makes "nothing
 * persisted" a direct consequence of never having written anything, rather than something
 * that has to be undone afterwards: no savepoint, no internal exception/catch is needed to
 * get the same observable guarantee (an empty {@code needPayload} batch commits nothing of
 * substance, an empty response commits an effectively no-op transaction). Genuine failures
 * (e.g. a duplicate version_id) still propagate as a thrown {@link SQLException} out of
 * {@link #write}, which is what actually drives the rollback of a partially-applied batch
 * via {@code Tx.inTenantTx}'s catch block — that path is unchanged and is what the
 * atomicity gate test exercises.
 *
 * <p>All calls run on a connection already inside {@code Tx.inTenantTx}.
 */
public final class WriteService
{
    private WriteService()
    {
    }

    public static WriteBatchResponse write( Connection connection, WriteBatchRequest request )
        throws SQLException
    {
        long repoKey = RepoKeys.resolve( connection, request.repo() );

        List<PayloadRef.Inline> inlinePayloads = new ArrayList<>();
        List<String> needPayload = new ArrayList<>();
        for ( PayloadRef ref : request.payloads() )
        {
            switch ( ref )
            {
                case PayloadRef.Inline inline -> inlinePayloads.add( inline );
                case PayloadRef.HashOnly hashOnly ->
                {
                    if ( !PayloadStore.hasPayload( connection, hashOnly.hash() ) )
                    {
                        needPayload.add( hashOnly.hash() );
                    }
                }
            }
        }

        if ( !needPayload.isEmpty() )
        {
            return new WriteBatchResponse( null, List.copyOf( needPayload ) );
        }

        for ( PayloadRef.Inline inline : inlinePayloads )
        {
            PayloadStore.putPayload( connection, inline.bytes() );
        }
        for ( var version : request.versions() )
        {
            VersionStore.store( connection, repoKey, version );
        }
        for ( BranchEntryRecord entry : request.branchEntries() )
        {
            BranchStore.store( connection, repoKey, entry );
        }
        if ( request.commit() != null )
        {
            CommitStore.store( connection, repoKey, request.commit() );
        }

        Long maxSeq = insertOutboxRows( connection, repoKey, request.branchEntries() );
        return new WriteBatchResponse( maxSeq, List.of() );
    }

    private static Long insertOutboxRows( Connection connection, long repoKey, List<BranchEntryRecord> entries )
        throws SQLException
    {
        if ( entries.isEmpty() )
        {
            return null;
        }
        Long maxSeq = null;
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO outbox (repo_key, branch, node_id, version_id, op) VALUES (?, ?, ?, ?, 'INDEX') RETURNING seq" ))
        {
            for ( BranchEntryRecord entry : entries )
            {
                statement.setLong( 1, repoKey );
                statement.setString( 2, entry.branch() );
                statement.setString( 3, entry.nodeId() );
                statement.setString( 4, entry.versionId() );
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

    /**
     * Ephemeral-branch fork (DESIGN.md §5): copies {@code branch_entry} rows from
     * {@code sourceBranch} to {@code targetBranch} via {@code INSERT..SELECT} — versions
     * and payloads are shared by content hash/id, so no new version or payload rows are
     * created. Emits one outbox row (op INDEX) per copied entry under the target branch,
     * and returns the max seq written (or {@code null} if the source branch was empty).
     */
    public static Long forkBranch( Connection connection, long repoKey, String sourceBranch, String targetBranch )
        throws SQLException
    {
        try (PreparedStatement statement =
                 connection.prepareStatement( "INSERT INTO branch (repo_key, branch) VALUES (?, ?) ON CONFLICT DO NOTHING" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, targetBranch );
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO branch_entry (repo_key, branch, node_id, version_id, node_path, ts)
            SELECT repo_key, ?, node_id, version_id, node_path, ts
            FROM branch_entry WHERE repo_key = ? AND branch = ?
            """ ) )
        {
            statement.setString( 1, targetBranch );
            statement.setLong( 2, repoKey );
            statement.setString( 3, sourceBranch );
            statement.executeUpdate();
        }

        Long maxSeq = null;
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO outbox (repo_key, branch, node_id, version_id, op)
            SELECT repo_key, branch, node_id, version_id, 'INDEX'
            FROM branch_entry WHERE repo_key = ? AND branch = ?
            RETURNING seq
            """ ) )
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, targetBranch );
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
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
}
