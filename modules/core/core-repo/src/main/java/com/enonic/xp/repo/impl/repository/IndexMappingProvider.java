package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexResource;
import com.enonic.xp.repository.RepositoryId;

public class IndexMappingProvider
{
    public static IndexMapping get( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceProvider provider )
    {
        final IndexResource mappingResource = provider.get( repositoryId, indexType, IndexResourceType.MAPPING );

        return new IndexMapping( mappingResource );
    }
}
