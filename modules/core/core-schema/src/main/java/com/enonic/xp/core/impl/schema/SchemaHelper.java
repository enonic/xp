package com.enonic.xp.core.impl.schema;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;

import java.io.InputStream;
import java.time.Instant;

public final class SchemaHelper
{
    public static Icon loadIcon( final Class clz, final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase();
        final Icon svgIcon =  doLoadIcon( clz, "image/svg+xml", filePath + ".svg" );

        if ( svgIcon != null )
        {
            return svgIcon;
        }
        else
        {
            return doLoadIcon( clz, "image/png", filePath + ".png" );
        }
    }

    private static Icon doLoadIcon( final Class clz, final String mimeType, final String filePath )
    {
        try ( final InputStream stream = clz.getResourceAsStream( filePath ) )
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, mimeType, Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

    public static boolean isSystem( final ApplicationKey key )
    {
        return ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key );
    }

    public static Icon loadIcon( final Resource resource, final String mimeType )
    {
        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), mimeType, modifiedTime );
    }
}
