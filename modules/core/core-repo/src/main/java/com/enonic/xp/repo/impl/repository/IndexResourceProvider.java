package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public interface IndexResourceProvider
{
    IndexMapping getMapping( RepositoryId repositoryId, IndexType indexType );

    IndexSettings getSettings( RepositoryId repositoryId, IndexType indexType );
}
