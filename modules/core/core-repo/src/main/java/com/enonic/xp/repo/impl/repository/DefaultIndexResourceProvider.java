package com.enonic.xp.repo.impl.repository;

import java.net.URL;

import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexResourceType;
import com.enonic.xp.repository.IndexSettings;

public class DefaultIndexResourceProvider
    implements IndexResourceProvider
{
    private final String resourceBaseFolder;

    public DefaultIndexResourceProvider( final String resourceBaseFolder )
    {
        this.resourceBaseFolder = resourceBaseFolder;
    }

    @Override
    public IndexMapping getMapping( final IndexType indexType )
    {
        return IndexMapping.from( JsonHelper.toMap( JsonHelper.from( getResource( indexType, IndexResourceType.MAPPING ) ) ) );
    }

    @Override
    public IndexSettings getSettings( final IndexType indexType )
    {

        return IndexSettings.from( JsonHelper.toMap( JsonHelper.from( getResource( indexType, IndexResourceType.SETTINGS ) ) ) );
    }

    private URL getResource( final IndexType indexType, final IndexResourceType type )
    {
        String fileName =
            resourceBaseFolder + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            return DefaultIndexResourceProvider.class.getResource( fileName );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] default from file: " + fileName + " not found", e );
        }
    }
}
