package com.enonic.xp.repo.impl.repository;

import java.net.URL;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexResourceType;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.JsonHelper;

public class DefaultIndexResourceProvider
    implements IndexResourceProvider
{
    private final String resourceBaseFolder;

    public DefaultIndexResourceProvider( final String resourceBaseFolder )
    {
        this.resourceBaseFolder = resourceBaseFolder;
    }

    @Override
    public IndexMapping getMapping( final RepositoryId repositoryId, final IndexType indexType )
    {
        final JsonNode defaultMapping = getResource( indexType, IndexResourceType.MAPPING );

        return new IndexMapping( defaultMapping );
    }

    @Override
    public IndexSettings getSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        final JsonNode defaultSettings = getResource( indexType, IndexResourceType.SETTINGS );

        return new IndexSettings( defaultSettings );
    }

    private JsonNode getResource( final IndexType indexType, final IndexResourceType type )
    {
        String fileName =
            resourceBaseFolder + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            final URL resource = DefaultIndexResourceProvider.class.getResource( fileName );
            return JsonHelper.from( Objects.requireNonNull( resource ) );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] default from file: " + fileName + " not found", e );
        }
    }
}
