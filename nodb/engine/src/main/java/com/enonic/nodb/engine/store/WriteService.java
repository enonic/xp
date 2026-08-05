package com.enonic.nodb.engine.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.RepoRef;

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
 * <p><b>FK ordering (Phase 3 Gate A, BUILD-PHASE-3.md #10b):</b> the payload-insert loop
 * below runs strictly before the version-insert loop, in the same transaction. This was
 * already required for correctness before {@code node_version.node_data_hash} /
 * {@code index_config_hash} / {@code acl_hash} carried a {@code REFERENCES payload (hash)}
 * constraint; now it is load-bearing — reordering these two loops (or a caller supplying a
 * version whose hash was never inlined/hash-only-referenced in the same batch and isn't
 * already stored) surfaces as a {@code foreign_key_violation} (SQLSTATE 23503) thrown out
 * of the version-insert statement, which still rolls back the whole transaction via {@code
 * Tx.inTenantTx}'s catch block (nothing partially persists either way) but is a genuine
 * error — mapped to {@code FAILED_PRECONDITION} at the gRPC boundary (see
 * {@code NodeStoreService#mapSqlException}) — distinct from the clean, pre-checked {@code
 * needPayload} response above for a hash explicitly referenced via {@link
 * PayloadRef.HashOnly}.
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

    /**
     * One INDEX outbox row per branch-entry change, in the same transaction as the rows themselves
     * — the invariant DESIGN §3.3 rests on ("the search index can lag but can never miss a
     * committed write").
     *
     * <p>Also reachable from the per-op paths below (Phase 4 Gate A, risk 10a): {@code WriteBatch}
     * was the ONLY path that emitted, so a node written through the standalone
     * {@code StoreBranchEntry}/{@code DeleteBranchEntries} RPCs committed to Postgres and was never
     * seen by the indexer. Nothing errored — the node simply never appeared in search, which is the
     * failure mode a lagging-but-never-missing contract exists to make impossible.
     */
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
     * Per-op branch-entry upsert WITH outbox emission (risk 10a) — the standalone equivalent of
     * one {@link #write} branch entry, for {@code StoreBranchEntry}.
     *
     * <p>Returns the emitted seq so the RPC can populate {@code Ack.outbox_seq} and the caller can
     * {@code awaitRefresh} on it. Before this gate that field was documented as "only WriteBatch
     * populates this meaningfully today", with the per-op changefeed/outbox story explicitly
     * deferred; this closes it.
     */
    public static Long storeBranchEntry( Connection connection, RepoRef repo, BranchEntryRecord entry )
        throws SQLException
    {
        long repoKey = RepoKeys.resolve( connection, repo );
        BranchStore.store( connection, repoKey, entry );
        return insertOutboxRows( connection, repoKey, List.of( entry ) );
    }

    /**
     * Per-op branch-entry delete WITH outbox emission (risk 10a), for {@code DeleteBranchEntries}.
     *
     * <p>Also removes the shipped search documents, in the SAME transaction: leaving them behind
     * would make Gate G's rebuild resurrect deleted nodes, since a rebuild replays
     * {@code search_document} rather than {@code branch_entry}. The DELETE outbox rows then remove
     * them from the live index too.
     */
    public static Long deleteBranchEntries( Connection connection, RepoRef repo, String branch, List<String> nodeIds )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return null;
        }
        long repoKey = RepoKeys.resolve( connection, repo );
        BranchStore.delete( connection, repoKey, branch, nodeIds );
        deleteSearchDocuments( connection, repoKey, branch, nodeIds );

        Long maxSeq = null;
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO outbox (repo_key, branch, node_id, op) VALUES (?, ?, ?, 'DELETE') RETURNING seq" ))
        {
            for ( String nodeId : nodeIds )
            {
                statement.setLong( 1, repoKey );
                statement.setString( 2, branch );
                statement.setString( 3, nodeId );
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
     * Inlined rather than calling {@code engine.search.SearchDocumentStore}: this package is the
     * write path and must not depend on the search package (the search package already depends on
     * {@code store} for {@link RepoKeys}, and a cycle between the two would be the start of the
     * kind of tangle DESIGN §8's "explicit wiring" rule exists to prevent).
     */
    private static void deleteSearchDocuments( Connection connection, long repoKey, String branch, List<String> nodeIds )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM search_document WHERE repo_key = ? AND branch = ? AND node_id = ANY(?)" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            statement.executeUpdate();
        }
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
