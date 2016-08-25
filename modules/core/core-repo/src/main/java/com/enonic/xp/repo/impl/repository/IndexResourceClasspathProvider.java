package com.enonic.xp.repo.impl.repository;

import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Resources;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repository.IndexResource;
import com.enonic.xp.repository.JsonIndexResource;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.JsonHelper;

public class IndexResourceClasspathProvider
    implements IndexResourceProvider
{
    private final String resourceBaseFolder;

    public IndexResourceClasspathProvider( final String resourceBaseFolder )
    {
        this.resourceBaseFolder = resourceBaseFolder;
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

    private JsonNode getDefaultMapping( final IndexType indexType, final IndexResourceType type )
    {
        String fileName =
            resourceBaseFolder + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            return JsonHelper.from( Resources.getResource( IndexResourceClasspathProvider.class, fileName ) );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] default from file: " + fileName + " not found", e );
        }
    }


    private IndexResource doGetResource( final RepositoryId repositoryId, final IndexType indexType, final boolean includeDefault,
                                         final IndexResourceType type )
    {
        String resourceName = resolveResourceName( repositoryId, indexType, type );

        try
        {
            final URL resource = Resources.getResource( IndexResourceClasspathProvider.class, resourceName );
            final JsonNode settings = JsonHelper.from( resource );

            if ( includeDefault )
            {
                return new JsonIndexResource( mergeWithDefault( settings, indexType, type ) );
            }

            return new JsonIndexResource( settings );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] for repositoryId " + repositoryId + " from resource [" + resourceName + "] not found",
                                      e );
        }
    }

    private String resolveResourceName( final RepositoryId repositoryId, final IndexType indexType, final IndexResourceType type )
    {
        return resourceBaseFolder + "/" + type.getName() + "/" + repositoryId.toString() + "/" + indexType.getName() + "-" +
            type.getName() +
            ".json";
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
