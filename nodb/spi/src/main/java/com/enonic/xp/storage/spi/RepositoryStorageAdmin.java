package com.enonic.xp.storage.spi;

import java.util.List;

/**
 * Repository storage lifecycle. Replaces the admin half of IndexServiceInternal.
 * ES-shaped concepts (waitForYellowStatus, isMaster, close/open index, putMapping)
 * intentionally do NOT cross this SPI — they are backend-internal or obsolete:
 * cluster-singleton concerns move to the control plane / DB leases.
 */
public interface RepositoryStorageAdmin
{
    void createRepository( RepoRef repo, RepositorySettingsRecord settings );

    void deleteRepository( RepoRef repo );

    boolean repositoryExists( RepoRef repo );

    List<RepoRef> listRepositories( TenantRef tenant );

    /** Per-repo storage statistics (row/doc counts, bytes) — the metering read point. */
    RepositoryStats stats( RepoRef repo );
}
