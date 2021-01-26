package com.enonic.xp.impl.macro;

import java.io.InputStream;
import java.time.Instant;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
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
            return Icon.from( stream, mimeType, Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

    public static Icon loadIcon( final MacroKey macroKey, final ResourceService resourceService, final String path )
    {
        final Icon svgIcon = loadIcon( macroKey, "image/svg+xml", "svg", resourceService, path );

        if ( svgIcon != null )
        {
            return svgIcon;
        }
        else
        {
            return loadIcon( macroKey, "image/png", "png", resourceService, path );
        }
    }

    private static Icon loadIcon( final MacroKey macroKey, final String mimeType, final String ext, final ResourceService resourceService,
                                  final String path )
    {
        final ResourceKey resourceKey = toResourceKey( macroKey, ext, path );
        final Resource resource = resourceService.getResource( resourceKey );
        return doLoadIcon( resource, mimeType );
    }

    private static ResourceKey toResourceKey( final MacroKey macroKey, final String ext, final String path )
    {
        final ApplicationKey appKey = macroKey.getApplicationKey();
        final String localName = macroKey.getName();
        return ResourceKey.from( appKey, path + "/" + localName + "/" + localName + "." + ext );
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
