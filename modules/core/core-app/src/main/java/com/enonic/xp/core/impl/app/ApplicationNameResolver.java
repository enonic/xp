package com.enonic.xp.core.impl.app;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

class ApplicationNameResolver
{
    public static String resolve( final ByteSource byteSource )
        throws Exception
    {
        File tmpFile = null;

        try
        {
            tmpFile = writeAsTmpFile( byteSource );

            return findSymbolicName( tmpFile );
        }
        finally
        {
            if ( tmpFile != null )
            {
                tmpFile.delete();
            }
        }
    }

    private static String findSymbolicName( final File file )
        throws Exception
    {
        final Manifest man;

        try (JarFile jarFile = new JarFile( file ))
        {
            if ( !ApplicationHelper.isApplication( jarFile ) )
            {
                throw new ApplicationInvalidException( "Not a valid application, must contain either site/site.xml-file or header " );
            }

            man = jarFile.getManifest();

            if ( man == null )
            {
                throw new ApplicationInvalidException( "Not a valid application, manifest not found" );
            }
        }

        return man.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME );
    }

    private static File writeAsTmpFile( final ByteSource byteSource )
        throws IOException
    {
        File targetFile = File.createTempFile( "application", ".jar" );
        byteSource.copyTo( Files.asByteSink( targetFile ) );

        return targetFile;
    }

}
