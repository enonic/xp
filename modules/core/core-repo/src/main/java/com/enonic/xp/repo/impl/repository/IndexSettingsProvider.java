package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexResource;
import com.enonic.xp.repository.RepositoryId;

public class IndexSettingsProvider
{
    public static IndexResource get( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceProvider provider )
    {
        return provider.get( repositoryId, indexType, IndexResourceType.SETTINGS );
    }
}
