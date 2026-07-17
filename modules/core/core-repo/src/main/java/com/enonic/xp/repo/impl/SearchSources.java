package com.enonic.xp.repo.impl;

import com.enonic.xp.storage.spi.SingleRepoSearchSource;

/**
 * Builds {@link SingleRepoSearchSource} (storage SPI) from an {@link InternalContext}
 * (core-repo internal) — kept here rather than as a static factory on the SPI type itself,
 * since {@code InternalContext} is core-repo internal and must not be a dependency of
 * core-storage-spi.
 */
public final class SearchSources
{
    private SearchSources()
    {
    }

    public static SingleRepoSearchSource from( final InternalContext context )
    {
        return SingleRepoSearchSource.create()
            .repositoryId( context.getRepositoryId() )
            .branch( context.getBranch() )
            .acl( context.getPrincipalKeys() )
            .build();
    }
}
