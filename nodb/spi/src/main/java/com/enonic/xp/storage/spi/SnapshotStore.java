package com.enonic.xp.storage.spi;

import java.util.List;

/**
 * Logical snapshot/restore, replacing the ES fs-snapshot implementation behind
 * com.enonic.xp.snapshot.SnapshotService (whose public API is unchanged).
 *
 * NoDB semantics: a snapshot is a consistent logical export of a repo's rows
 * (repeatable-read) plus a binary manifest and the outbox position. The search index
 * is never snapshotted — restore loads rows and re-feeds/rebuilds the index.
 */
public interface SnapshotStore
{
    SnapshotRecord snapshot( RepoRef repo, String snapshotName );

    void restore( RepoRef repo, String snapshotName );

    List<SnapshotRecord> list( TenantRef tenant );

    void delete( TenantRef tenant, Collection<String> snapshotNames );
}
