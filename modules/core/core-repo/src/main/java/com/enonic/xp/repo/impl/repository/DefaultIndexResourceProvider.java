package com.enonic.xp.repo.impl.repository;

import java.net.URL;
import java.util.Objects;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexResourceType;
import com.enonic.xp.repository.IndexSettings;

public final class DefaultIndexResourceProvider
{
    private static final String DEFAULT_INDEX_RESOURCE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    public static final DefaultIndexResourceProvider INSTANCE = new DefaultIndexResourceProvider();

    public IndexMapping getMapping( final IndexType indexType )
    {
        final URL defaultMapping = getResource( indexType, IndexResourceType.MAPPING );

        return IndexMapping.from( defaultMapping );
    }

    public IndexSettings getSettings( final IndexType indexType )
    {
        final URL defaultSettings = getResource( indexType, IndexResourceType.SETTINGS );

        return IndexSettings.from( defaultSettings );
    }

    private URL getResource( final IndexType indexType, final IndexResourceType type )
    {
        String fileName =
            DEFAULT_INDEX_RESOURCE_FOLDER + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            return Objects.requireNonNull( DefaultIndexResourceProvider.class.getResource( fileName ) );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] default from file: " + fileName + " not found", e );
        }
    }
}
