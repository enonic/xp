package com.enonic.xp.repo.impl.index;

/**
 * Elasticsearch-cluster-shaped operations that intentionally stay backend-internal and do
 * NOT cross the storage SPI (Phase 0, Gate C — see {@code nodb/BUILD-PHASE-0.md}): cluster
 * health/master-node checks, and closing/opening/bulk-deleting indices by raw name across an
 * arbitrary, possibly multi-repository set — needed as-is by
 * {@code com.enonic.xp.repo.impl.elasticsearch.snapshot.SnapshotServiceImpl} /
 * {@code SnapshotRestoreExecutor} during snapshot restore (explicitly out of scope for the
 * storage-SPI extraction, see DESIGN.md).
 * <p>
 * Everything else that used to live on this interface (per-repository index lifecycle:
 * create/delete/exists/refresh/settings/mapping) now lives behind
 * {@code com.enonic.xp.storage.spi.RepositoryStorageAdmin} (storage-<repo> index) and
 * {@code com.enonic.xp.storage.spi.NodeSearchIndex} (search-<repo> index).
 * <p>
 * Consumers of this narrow interface: {@code SnapshotServiceImpl}
 * (waitForYellowStatus, deleteIndices), {@code SnapshotRestoreExecutor} (closeIndices,
 * deleteIndices, openIndices), {@code SystemRepoInitializer} (isMaster, waitForYellowStatus,
 * gating initialization to the elected master node), {@code NodeRepositoryServiceImpl} and
 * {@code IndexServiceImpl} (waitForYellowStatus/closeIndices/openIndices/isMaster around
 * their own {@code RepositoryStorageAdmin}/{@code NodeSearchIndex} calls).
 */
public interface IndexServiceInternal
{
    void deleteIndices( String... indexNames );

    void closeIndices( String... indices );

    void openIndices( String... indices );

    boolean waitForYellowStatus( String... indexNames );

    boolean isMaster();
}
