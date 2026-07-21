package com.enonic.xp.storage.spi;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * The three payload segments belonging to a single node version — node-data, index-config
 * and ACL — carried alongside a {@link VersionRecord} into {@link NodeStore#storeVersion}
 * and {@link NodeStore#storeNode} (Phase 3 Gate B, nodb/BUILD-PHASE-3.md §"symmetric B").
 * All three are always present (mirrors {@code NodeVersionKey}, which requires all three
 * blob keys non-null); whether each carries new bytes or is hash-only is per-segment, see
 * {@link PayloadSegment}.
 */
@NullMarked
public record NodeSegments(PayloadSegment nodeData, PayloadSegment indexConfig, PayloadSegment accessControl)
{
    public NodeSegments
    {
        requireNonNull( nodeData );
        requireNonNull( indexConfig );
        requireNonNull( accessControl );
    }
}
