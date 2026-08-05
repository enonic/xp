package com.enonic.xp.storage.spi;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Bounded query surface for {@link NodeStore#findVersions} (nodb/BUILD-PHASE-3.5.md Gate
 * A) — deliberately NOT a general filter/query AST: exactly the option set the enumerated
 * storage-source callers need. History-by-node ({@code GetNodeVersionsCommand}: order
 * {@link Order#TS_DESC_ID_ASC} + keyset {@link Cursor}), dump/vacuum range scans
 * ({@code RepoDumper}: ts floor + size -1; {@code VersionTableVacuumCommand}: ts ceiling +
 * {@code versionIdAfter} keyset, order {@link Order#ID_ASC}) and blob-key usage checks
 * ({@code SegmentVacuum}/{@code IsBlobUsedByVersion}: {@link BlobKeyTerm}, size 0).
 * <p>
 * Bound semantics mirror those callers' ES filters exactly: {@code tsFloor} is inclusive
 * ({@code RangeFilter.from}), {@code tsCeiling} is inclusive ({@code RangeFilter.to}),
 * {@code versionIdAfter} is exclusive ({@code RangeFilter.gt}). {@code null} means "no
 * predicate". {@code size} 0 = count-only, -1 = all rows; {@code from}/{@code size} page
 * otherwise — {@link VersionQueryResult#totalHits} is accurate regardless.
 */
@NullMarked
public record VersionQuery(@Nullable String nodeId, @Nullable Instant tsFloor, @Nullable Instant tsCeiling,
                            @Nullable String versionIdAfter, @Nullable BlobKeyTerm blobKeyTerm, @Nullable Cursor cursor, Order order,
                            int from, int size)
{
    public VersionQuery
    {
        requireNonNull( order );
    }

    public enum Order
    {
        /** History order: {@code ts DESC, version_id ASC} (the equal-ts tiebreaker). */
        TS_DESC_ID_ASC,
        /** Vacuum keyset order. */
        ID_ASC,
        /** No ordering — count-only and containment checks. */
        UNORDERED
    }

    /** Which version field a blob-key term matches: {@code binary_keys} array containment vs {@code node_data_hash} equality. */
    public enum BlobKeyField
    {
        BINARY_KEYS, NODE_DATA_HASH
    }

    public record BlobKeyTerm(String blobKey, BlobKeyField field)
    {
        public BlobKeyTerm
        {
            requireNonNull( blobKey );
            requireNonNull( field );
        }
    }

    /** Keyset continuation for {@link Order#TS_DESC_ID_ASC}: the (ts, versionId) of the last row already seen (exclusive). */
    public record Cursor(Instant ts, String versionId)
    {
        public Cursor
        {
            requireNonNull( ts );
            requireNonNull( versionId );
        }
    }
}
