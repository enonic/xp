package com.enonic.nodb.engine.model;

import java.time.Instant;

/**
 * Bounded query surface for {@code node_version} reads (Phase 3.5 Gate A, the enumerated
 * findVersions callers pinned by Gate 0 in nodb/BUILD-PHASE-3.5.md): history-by-node with
 * keyset paging, dump/vacuum range scans, and blob-key usage checks. Every field except
 * {@code order}/{@code from}/{@code size} is optional ({@code null} = no predicate).
 *
 * <p>Bound semantics mirror the XP callers exactly: {@code tsFloor} is inclusive
 * ({@code ts >= floor}, RepoDumper's {@code RangeFilter.from}), {@code tsCeiling} is
 * inclusive ({@code ts <= ceiling}, VersionTableVacuum's {@code RangeFilter.to}), and
 * {@code versionIdAfter} is exclusive ({@code version_id > after}, the vacuum keyset's
 * {@code RangeFilter.gt}). {@code cursor} is the exclusive keyset continuation for
 * {@link Order#TS_DESC_ID_ASC}: rows strictly after the cursor row in that order.
 *
 * <p>{@code size} follows the storage-query convention: {@code 0} = count-only (accurate
 * totalHits, no rows), {@code -1} = all rows; {@code from}/{@code size} page otherwise.
 * totalHits is accurate regardless of paging.
 */
public record VersionQuery(String nodeId, Instant tsFloor, Instant tsCeiling, String versionIdAfter, BlobKeyTerm blobKeyTerm,
                            Cursor cursor, Order order, int from, int size)
{
    public enum Order
    {
        /** History order: {@code ts DESC, version_id ASC} (equal-ts tiebreaker) — served by node_version_by_node_v2. */
        TS_DESC_ID_ASC,
        /** Vacuum keyset order. */
        ID_ASC,
        /** No ORDER BY — count-only and containment checks. */
        UNORDERED
    }

    /** Which column a blob-key term matches: array containment vs plain equality. */
    public enum BlobKeyField
    {
        BINARY_KEYS, NODE_DATA_HASH
    }

    public record BlobKeyTerm(String blobKey, BlobKeyField field)
    {
    }

    /** Keyset continuation for {@link Order#TS_DESC_ID_ASC}: the (ts, version_id) of the last row already seen. */
    public record Cursor(Instant ts, String versionId)
    {
    }
}
