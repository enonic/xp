package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexResource;
import com.enonic.xp.repository.RepositoryId;

public interface IndexResourceProvider
{
    IndexResource get( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceType type );

    IndexResource get( final RepositoryId repositoryId, final IndexType indexType, final boolean includeDefault,
                       final IndexResourceType type );
}
