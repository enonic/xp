package com.enonic.nodb.engine.model;

/**
 * A stored payload row: content hash ({@code "sha256:<hex>"}) + raw bytes — mirrors
 * {@code schema.sql}'s {@code payload} table 1:1. Used by {@link
 * com.enonic.nodb.engine.store.PayloadStore#getPayloads} (Phase 3 Gate A batched
 * multi-hash read, DESIGN.md §2.1 bulk-read requirement) since a single hash string alone
 * isn't enough to report back which of several requested hashes a row corresponds to.
 */
public record PayloadRecord(String hash, byte[] bytes)
{
}
