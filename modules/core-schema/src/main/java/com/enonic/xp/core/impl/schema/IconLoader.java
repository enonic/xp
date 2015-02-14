package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import org.osgi.framework.Bundle;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.xp.core.icon.Icon;

public final class IconLoader
{
    private static final String ICON_FILE_NAME = "thumb";

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

        try
        {
            final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
            return Icon.from( Resources.toByteArray( url ), MediaType.PNG.toString(), modifiedTime );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Failed to close icon file with resource from: " + url.toString(), e );
        }

    }

    private URL findIconUrl( final String parentPath )
    {
        final String path = parentPath + "/" + ICON_FILE_NAME + ".png";
        return this.bundle.getEntry( path );
    }
}
