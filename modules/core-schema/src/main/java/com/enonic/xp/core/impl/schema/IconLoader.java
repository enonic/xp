package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.xp.icon.Icon;

public final class IconLoader
{
    private static final String EXTENSION = ".png";

    private final static Pattern PATH_PATTERN = Pattern.compile( ".*/([^/]+)" );

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
        final String path = parentPath + "/" + getIconFileNameFromPath( parentPath ) + EXTENSION;
        return this.bundle.getEntry( path );
    }

    private String getIconFileNameFromPath( final String path )
    {
        final Matcher matcher = PATH_PATTERN.matcher( path );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }
}
