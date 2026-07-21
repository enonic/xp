package com.enonic.xp.storage.spi;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * One of a version's three content-addressed payload segments (node-data, index-config,
 * ACL — see {@link NodeSegments}), as carried into {@link NodeStore#storeVersion} /
 * {@link NodeStore#storeNode} (Phase 3 Gate B, nodb/BUILD-PHASE-3.md).
 * <p>
 * {@code bytes} is {@code null} when the caller already knows this exact hash is stored
 * (the segment's content did not change — e.g. {@code VersionServiceImpl}'s commit/
 * change-attributes paths, which reuse an existing {@code NodeVersionKey} verbatim): a
 * hash-only segment lets a transactional backend (nodb) reference the existing payload row
 * by hash without re-sending bytes over the wire, while a blob-backed backend
 * (elasticsearch) simply has nothing new to write for it. Non-null {@code bytes} means
 * "persist this content under this hash if not already present" — every implementation is
 * expected to dedup by hash (nodb: {@code ON CONFLICT DO NOTHING}; elasticsearch/BlobStore:
 * content-addressed {@code addRecord} is already idempotent).
 */
@NullMarked
public record PayloadSegment(String hash, byte @Nullable [] bytes)
{
    public PayloadSegment
    {
        requireNonNull( hash );
    }
}
