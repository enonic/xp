package com.enonic.xp.repo.impl.repository;

import java.net.URL;

import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexResourceType;

public class DefaultIndexResourceProvider
    implements IndexResourceProvider
{
    private static final String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

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
        final String fileName =
            DEFAULT_INDEX_RESOURCE_FOLDER + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() +
                ".json";

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
