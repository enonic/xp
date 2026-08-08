package com.enonic.nodb.engine.snapshot;

import java.time.Instant;

/**
 * One {@code snapshot} registry row (migration 004) — mirrors the table columns 1:1. The
 * snapshot BYTES live in object storage under {@link #location()}; this record is only the
 * registry metadata. Count/size/manifest columns are {@code null} while {@link #state()} is
 * {@code CREATING} or {@code FAILED} — they are stamped by the COMPLETE update, never
 * before every artifact is durably written.
 */
public record SnapshotRecord(String snapshotId, String scope, String repoId, Long repoKey, Instant createdAt, Instant expiresAt,
                             long outboxSeq, String state, String location, int formatVersion, Long versionCount, Long headCount,
                             Long commitCount, Long documentCount, Long hashCount, Long totalBytes, String manifestSha256)
{
}
