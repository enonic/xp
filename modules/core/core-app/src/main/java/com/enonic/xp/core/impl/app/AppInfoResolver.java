package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;

class AppInfoResolver
{
    public static final String APPLICATION_YML = "application.yml";

    public static final String APPLICATION_YAML = "application.yaml";

    public static AppInfo resolve( final ByteSource byteSource )
        throws Exception
    {
        final Path tmpFile = Files.createTempFile( "application", ".jar" );
        try
        {
            byteSource.copyTo( MoreFiles.asByteSink( tmpFile ) );
            return findAppInfo( tmpFile );
        }
        finally
        {
            Files.deleteIfExists( tmpFile );
        }
    }

    private static AppInfo findAppInfo( final Path file )
        throws Exception
    {
        final Manifest man;
        final String descriptorYaml;

        try (JarFile jarFile = new JarFile( file.toFile() ))
        {
            man = jarFile.getManifest();

            if ( man == null )
            {
                throw new ApplicationInvalidException( "Not a valid application. Manifest not found" );
            }

            descriptorYaml = readDescriptorYaml( jarFile );

            if ( descriptorYaml == null && !hasValidApplicationHeader( man ) )
            {
                throw new ApplicationInvalidException( "Not a valid application." );
            }

        }
        final AppInfo appInfo = new AppInfo();
        final Attributes attrs = man.getMainAttributes();
        appInfo.name = attrs.getValue( Constants.BUNDLE_SYMBOLICNAME );
        appInfo.version = Optional.ofNullable( attrs.getValue( Constants.BUNDLE_VERSION ) ).orElse( "0.0.0" );
        appInfo.system = "system".equals( attrs.getValue( ApplicationManifestConstants.X_BUNDLE_TYPE ) );

        Optional.ofNullable( attrs.getValue( ApplicationManifestConstants.X_SYSTEM_VERSION ) )
            .map( ApplicationHelper::parseVersionRange )
            .ifPresent( vr -> {
                appInfo.minSystemVersion = vr.getLeft().toString();
                if ( vr.getRight() != null )
                {
                    appInfo.maxSystemVersion = vr.getRight().toString();
                }
            } );

        if ( descriptorYaml != null )
        {
            final ApplicationDescriptor descriptor =
                YmlApplicationDescriptorParser.parse( descriptorYaml, ApplicationKey.from( appInfo.name ) ).build();
            appInfo.title = descriptor.getTitle();
            appInfo.vendorName = descriptor.getVendorName();
        }
        else
        {
            appInfo.title = attrs.getValue( Constants.BUNDLE_NAME );
        }

        return appInfo;
    }

    public static boolean hasValidApplicationHeader( final Manifest manifest )
    {
        final String value = ApplicationHelper.getAttribute( manifest, ApplicationManifestConstants.X_BUNDLE_TYPE, "" );
        return ApplicationManifestConstants.APPLICATION_BUNDLE_TYPE.equals( value ) ||
            ApplicationManifestConstants.SYSTEM_BUNDLE_TYPE.equals( value );
    }

    private static String readDescriptorYaml( final JarFile jarFile )
        throws IOException
    {
        JarEntry entry = jarFile.getJarEntry( APPLICATION_YAML );
        if ( entry == null )
        {
            entry = jarFile.getJarEntry( APPLICATION_YML );
        }
        if ( entry == null )
        {
            return null;
        }
        try (InputStream is = jarFile.getInputStream( entry ))
        {
            return new String( is.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
