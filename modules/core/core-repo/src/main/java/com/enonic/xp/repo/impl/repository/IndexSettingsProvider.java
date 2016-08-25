package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public class IndexSettingsProvider
{
    public static IndexSettings get( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceProvider provider )
    {
        return new IndexSettings( provider.get( repositoryId, indexType, IndexResourceType.SETTINGS ) );
    }
}
