package com.enonic.wem.core.schema;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import org.apache.commons.io.FilenameUtils;
import org.osgi.framework.Bundle;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.wem.api.Icon;

public final class IconLoader
{
    private static final String ICON_FILE_NAME = "thumb";

    private final ImmutableMap<String, MediaType> extensionsMap = ImmutableMap.of( "jpeg", MediaType.JPEG, "png", MediaType.PNG );

    private final Bundle bundle;

    public IconLoader( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    public Icon readIcon( final String parentPath )
    {
        final URL url = findIconUrl( parentPath );
        if ( url == null )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
        final String ext = FilenameUtils.getExtension( url.getPath() );
        final MediaType mediaType = this.extensionsMap.get( ext );

        try
        {
            return Icon.from( Resources.toByteArray( url ), mediaType.toString(), modifiedTime );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Failed to close icon file with resource from: " + url.toString(), e );
        }

    }

    private URL findIconUrl( final String parentPath )
    {
        for ( final String ext : this.extensionsMap.keySet() )
        {
            final String path = parentPath + "/" + ICON_FILE_NAME + "." + ext;
            final URL url = this.bundle.getEntry( path );

            if ( url != null )
            {
                return url;
            }
        }

        return null;
    }
}
