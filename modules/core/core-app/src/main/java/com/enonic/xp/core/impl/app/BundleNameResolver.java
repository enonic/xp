package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;

public class BundleNameResolver
{
    public static String resolve( final ByteSource byteSource )
    {
        try
        {
            return doResolve( byteSource );
        }
        catch ( IOException e )
        {
            throw new ApplicationInstallException( "Cannot install application", e );
        }

    }

    private static String doResolve( final ByteSource byteSource )
        throws IOException
    {
        try (final InputStream in = byteSource.openStream())
        {
            try (final JarInputStream jarInputStream = new JarInputStream( in ))
            {
                return getNameFromManifest( jarInputStream );
            }
        }
    }

    private static String getNameFromManifest( final JarInputStream jarInputStream )
    {
        final Manifest manifest = jarInputStream.getManifest();

        if ( manifest == null )
        {
            throw new ApplicationInstallException( "Manifest-info not found in application" );
        }

        final String name = manifest.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME );

        if ( name == null )
        {
            throw new ApplicationInstallException(
                "Could not resolve name, missing value " + Constants.BUNDLE_SYMBOLICNAME + " in manifest" );
        }

        return name;
    }

}
