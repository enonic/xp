package com.enonic.xp.impl.macro;

import java.io.InputStream;
import java.time.Instant;

import com.enonic.xp.core.impl.schema.CmsResourceKeys;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;

public class IconLoader
{
    public static Icon loadIcon( final Class clz, final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase();
        final Icon svgIcon = doLoadIcon( clz, "image/svg+xml", filePath + ".svg" );

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
        try (InputStream stream = clz.getResourceAsStream( filePath ))
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, mimeType, Millis.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

    /**
     * Icon of the macro, next to its {@code descriptor}.
     */
    public static Icon loadIcon( final MacroKey macroKey, final Resource descriptor, final ResourceService resourceService )
    {
        final Icon svgIcon = loadIcon( macroKey, descriptor, "image/svg+xml", "svg", resourceService );

        if ( svgIcon != null )
        {
            return svgIcon;
        }
        else
        {
            return loadIcon( macroKey, descriptor, "image/png", "png", resourceService );
        }
    }

    private static Icon loadIcon( final MacroKey macroKey, final Resource descriptor, final String mimeType, final String ext,
                                  final ResourceService resourceService )
    {
        final Resource resource =
            resourceService.getResource( CmsResourceKeys.siblingKey( macroKey.getApplicationKey(), descriptor.getKey(), ext ) );
        return doLoadIcon( resource, mimeType );
    }

    private static Icon doLoadIcon( final Resource resource, final String mimeType )
    {
        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), mimeType, modifiedTime );
    }
}
