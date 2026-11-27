package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;

import static com.google.common.base.Strings.isNullOrEmpty;

class AppInfoResolver
{
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
        try (JarFile jarFile = new JarFile( file.toFile() ))
        {
            final Manifest man = jarFile.getManifest();
            final String descriptorYaml = readDescriptorYaml( jarFile );
            final boolean hasCmsDescriptor =
                SchemaResourcePaths.CMS_DESCRIPTOR_PATHS.stream().anyMatch( path -> jarFile.getJarEntry( path ) != null );

            return man == null
                ? codelessAppInfo( jarFile, descriptorYaml, hasCmsDescriptor )
                : bundleAppInfo( man, descriptorYaml, hasCmsDescriptor );
        }
    }

    /**
     * A jar without a manifest is a codeless application: it is never installed as a bundle, its descriptor declares what the
     * manifest of a bundle declares, and it ships nothing but the resources served from the application repository.
     */
    private static AppInfo codelessAppInfo( final JarFile jarFile, final String descriptorYaml, final boolean hasCmsDescriptor )
    {
        if ( descriptorYaml == null )
        {
            throw new ApplicationInvalidException( "Not a valid application. Neither manifest nor application descriptor found" );
        }

        final ApplicationKey key = YmlApplicationDescriptorParser.name( descriptorYaml );
        if ( key == null )
        {
            throw new ApplicationInvalidException( "Not a valid codeless application. The application descriptor declares no name" );
        }

        final List<String> unservable = jarFile.stream()
            .filter( entry -> !entry.isDirectory() )
            .map( ZipEntry::getName )
            .filter( name -> !SchemaResourcePaths.SCHEMA_RESOURCE_PATTERN.matcher( name ).matches() )
            .toList();
        if ( !unservable.isEmpty() )
        {
            throw new ApplicationInvalidException(
                "Not a valid codeless application. It ships resources that are not served without a bundle: " + unservable );
        }

        final ApplicationDescriptor descriptor = YmlApplicationDescriptorParser.parse( descriptorYaml, key ).build();
        if ( isNullOrEmpty( descriptor.getVersion() ) )
        {
            throw new ApplicationInvalidException( "Not a valid codeless application. The application descriptor declares no version" );
        }

        final AppInfo appInfo = new AppInfo();
        appInfo.codeless = true;
        appInfo.hasCmsDescriptor = hasCmsDescriptor;
        appInfo.name = key.getName();
        appInfo.version = descriptor.getVersion();
        appInfo.minSystemVersion = descriptor.getMinSystemVersion();
        appInfo.maxSystemVersion = descriptor.getMaxSystemVersion();
        appInfo.title = descriptor.getTitle();
        appInfo.vendorName = descriptor.getVendorName();
        return appInfo;
    }

    private static AppInfo bundleAppInfo( final Manifest man, final String descriptorYaml, final boolean hasCmsDescriptor )
    {
        if ( descriptorYaml == null && !hasValidApplicationHeader( man ) )
        {
            throw new ApplicationInvalidException( "Not a valid application." );
        }

        final AppInfo appInfo = new AppInfo();
        appInfo.hasCmsDescriptor = hasCmsDescriptor;
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
        final JarEntry entry = ApplicationBundleUtils.DESCRIPTOR_PATHS.stream()
            .map( jarFile::getJarEntry )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
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
