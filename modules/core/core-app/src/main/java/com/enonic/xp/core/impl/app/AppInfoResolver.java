package com.enonic.xp.core.impl.app;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

class AppInfoResolver
{
    public static AppInfo resolve( final ByteSource byteSource )
        throws Exception
    {
        File tmpFile = null;

        try
        {
            tmpFile = writeAsTmpFile( byteSource );

            return findAppInfo( tmpFile );
        }
        finally
        {
            if ( tmpFile != null )
            {
                tmpFile.delete();
            }
        }
    }

    private static AppInfo findAppInfo( final File file )
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
        final AppInfo appInfo = new AppInfo();
        final Attributes attrs = man.getMainAttributes();
        appInfo.name = attrs.getValue( Constants.BUNDLE_SYMBOLICNAME );
        appInfo.displayName = attrs.getValue( Constants.BUNDLE_NAME );
        appInfo.vendorName = attrs.getValue( ApplicationManifestConstants.X_VENDOR_NAME );
        appInfo.version = Optional.ofNullable( attrs.getValue( Constants.BUNDLE_VERSION ) ).orElse( "0.0.0" );

        Optional.ofNullable( attrs.getValue( ApplicationManifestConstants.X_SYSTEM_VERSION ) )
            .map( ApplicationHelper::parseVersionRange )
            .ifPresent( vr -> {
                appInfo.minSystemVersion = vr.getLeft().toString();
                if (vr.getRight() != null) {
                    appInfo.maxSystemVersion =  vr.getRight().toString();
                }
            } );

        return appInfo;
    }

    private static File writeAsTmpFile( final ByteSource byteSource )
        throws IOException
    {
        File targetFile = File.createTempFile( "application", ".jar" );
        byteSource.copyTo( Files.asByteSink( targetFile ) );

        return targetFile;
    }

}
