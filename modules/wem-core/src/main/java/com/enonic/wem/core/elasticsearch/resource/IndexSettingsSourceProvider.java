package com.enonic.wem.core.elasticsearch.resource;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;


public class IndexSettingsSourceProvider
{

    private final static String PREFIX = "/META-INF/index/settings/";

    public static final String INDEX_SETTINGS_FILE_PATTERN = "-index-settings.json";

    public String getSource( final Index index )
    {

        final String fileName = createIndexSettingsFileName( index );

        try
        {
            final URL url = Resources.getResource( getClass(), fileName );
            return Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new IndexException( "Failed to load settings for index " + index + " from file: " + fileName, e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IndexException( "Settings for index " + index + " from file: " + fileName + " not found", e );
        }
    }

    private String createIndexSettingsFileName( final Index index )
    {
        return ( PREFIX + index.getName() + INDEX_SETTINGS_FILE_PATTERN );
    }

}
