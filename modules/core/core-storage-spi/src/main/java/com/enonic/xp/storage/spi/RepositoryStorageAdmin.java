package com.enonic.xp.storage.spi;

import java.util.List;

import com.enonic.xp.repository.RepositoryId;

/**
 * Repository storage lifecycle. Replaces the admin half of {@code IndexServiceInternal}.
 * ES-shaped concepts ({@code waitForYellowStatus}, {@code isMaster}, close/open index,
 * {@code putIndexMapping}) intentionally do NOT cross this SPI — they stay backend-internal
 * (see DESIGN.md §3.2/§C).
 */
public interface RepositoryStorageAdmin
{
    void createRepository( RepositoryId repositoryId );

    void deleteRepository( RepositoryId repositoryId );

    boolean repositoryExists( RepositoryId repositoryId );

    List<RepositoryId> listRepositories();
}
