package com.enonic.xp.repository;

import com.enonic.xp.index.IndexType;

public interface IndexResourceProvider
{
    IndexMapping getMapping( final RepositoryId repositoryId, final IndexType indexType );

    IndexSettings getSettings( final RepositoryId repositoryId, final IndexType indexType );
}
