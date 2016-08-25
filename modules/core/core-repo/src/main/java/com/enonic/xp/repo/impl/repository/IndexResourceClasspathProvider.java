package com.enonic.xp.repo.impl.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Resources;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repository.IndexResource;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.JsonHelper;

public class IndexResourceClasspathProvider
    extends AbstractIndexResourceProvider
{
    private final String baseFolder;

    public IndexResourceClasspathProvider( final String baseFolder )
    {
        this.baseFolder = baseFolder;
    }

    @Override
    public IndexResource get( final RepositoryId repositoryId, final IndexType indexType, final boolean includeDefault,
                              final IndexResourceType indexResourceType )
    {
        return doGetResource( repositoryId, indexType, includeDefault, indexResourceType );
    }

    @Override
    public IndexResource get( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceType resourceType )
    {
        return doGetResource( repositoryId, indexType, true, resourceType );
    }

    private IndexResource doGetResource( final RepositoryId repositoryId, final IndexType indexType, final boolean includeDefault,
                                         final IndexResourceType type )
    {
        String fileName =
            baseFolder + "/" + type.getName() + "/" + repositoryId.toString() + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            final JsonNode settings = JsonHelper.from( Resources.getResource( IndexResourceClasspathProvider.class, fileName ) );

            if ( includeDefault )
            {
                return new IndexResource( mergeWithDefault( settings, indexType, type ) );
            }

            return new IndexResource( settings );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] for repositoryId " + repositoryId + " from file: " + fileName + " not found", e );
        }
    }

    private JsonNode mergeWithDefault( final JsonNode indexSettings, final IndexType indexType, final IndexResourceType type )
    {
        if ( indexSettings == null )
        {
            return getDefaultMapping( indexType, type );
        }

        return JsonHelper.merge( getDefaultMapping( indexType, type ), indexSettings );
    }

}
