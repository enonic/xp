package com.enonic.xp.core.impl.app;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationType;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.security.SystemConstants;

import static com.enonic.xp.core.impl.app.ApplicationManifestConstants.X_CAPABILITY;
import static com.enonic.xp.core.impl.app.ApplicationManifestConstants.X_SOURCE_PATHS;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class ApplicationHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationHelper.class );

    private static final String LOCAL_BUNDLE_LOCATION_PREFIX = "local:";

    public static ApplicationKey getApplicationKey( final Bundle bundle )
    {
        return ApplicationKey.from( ApplicationBundleUtils.getApplicationName( bundle ) );
    }

    /**
     * Resolves the application type declared in the application descriptor (enonic.yaml) of the bundle.
     * Falls back to {@link ApplicationType#BUNDLE} when the bundle has no descriptor or the descriptor cannot be parsed.
     */
    static ApplicationType getApplicationType( final Bundle bundle )
    {
        final URL descriptorUrl = ApplicationBundleUtils.DESCRIPTOR_PATHS.stream()
            .filter( path -> bundle.getEntry( path ) != null )
            .findFirst()
            .map( bundle::getResource )
            .orElse( null );

        if ( descriptorUrl == null )
        {
            return ApplicationType.BUNDLE;
        }

        try (InputStream stream = descriptorUrl.openStream())
        {
            final String yaml = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
            return YmlApplicationDescriptorParser.parse( yaml, getApplicationKey( bundle ) ).build().getType();
        }
        catch ( Exception e )
        {
            LOG.warn( "Unable to resolve application type of [{}], assuming {}", bundle.getSymbolicName(), ApplicationType.BUNDLE, e );
            return ApplicationType.BUNDLE;
        }
    }

    /**
     * {@code true} when the bundle ships a cms descriptor ({@code cms/cms.yaml}), i.e. the application owns its schema.
     */
    static boolean hasCmsDescriptor( final Bundle bundle )
    {
        return SchemaResourcePaths.CMS_DESCRIPTOR_PATHS.stream().anyMatch( path -> bundle.getEntry( path ) != null );
    }

    /**
     * Location of an application bundle. Local applications are marked by a location prefix,
     * the only channel that reaches the bundle tracker creating the application.
     */
    static String toBundleLocation( final ApplicationKey applicationKey, final boolean local )
    {
        return ( local ? LOCAL_BUNDLE_LOCATION_PREFIX : "" ) + applicationKey.getName();
    }

    /**
     * {@code true} when the bundle was installed as a local application, see {@link #toBundleLocation(ApplicationKey, boolean)}.
     */
    static boolean isLocalApplication( final Bundle bundle )
    {
        final String location = bundle.getLocation();
        return location != null && location.startsWith( LOCAL_BUNDLE_LOCATION_PREFIX );
    }

    static String getAttribute( final Manifest manifest, final String name, final String defValue )
    {
        if ( manifest == null )
        {
            return defValue;
        }

        return manifest.getMainAttributes().getValue( name ) != null ? manifest.getMainAttributes().getValue( name ) : defValue;
    }

    static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return isNullOrEmpty( value ) ? defValue : value;
    }

    static Set<String> getCapabilities( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_CAPABILITY, "" );
        return ImmutableSet.copyOf( Splitter.on( ',' ).omitEmptyStrings().trimResults().split( value ) );
    }

    static List<String> getSourcePaths( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_SOURCE_PATHS, "" );
        if ( isNullOrEmpty( value ) )
        {
            return new ArrayList<>();
        }
        return Splitter.on( ',' ).trimResults().splitToList( value );
    }

    static VersionRange parseVersionRange( final String value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            return VersionRange.valueOf( value );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    public static void checkSystemVersion( final Bundle bundle, final Version systemVersion )
    {
        final String systemVersionRange = getHeader( bundle, ApplicationManifestConstants.X_SYSTEM_VERSION, "" );

        if ( !systemVersionRange.isEmpty() )
        {
            final VersionRange appVersionRange = parseVersionRange( systemVersionRange );
            if ( appVersionRange != null && appVersionRange.includes( systemVersion ) )
            {
                return;
            }
            throw new ApplicationInvalidVersionException( systemVersionRange, systemVersion );
        }
    }

    public static <T> T runAsAdmin( final Callable<T> callable )
    {
        return createAdminContext().callWith( callable );
    }

    public static void runAsAdmin( final Runnable runnable )
    {
        createAdminContext().runWith( runnable );
    }

    static Context createAdminContext()
    {
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( ApplicationConstants.APPLICATION_SU_AUTH_INFO )
            .build();
    }

    static <T> T callWithContext( Callable<T> runnable )
    {
        return createContext().callWith( runnable );
    }

    static void runWithContext( Runnable runnable )
    {
        createContext().runWith( runnable );
    }

    private static Context createContext()
    {
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( ContextAccessor.current().getAuthInfo() )
            .build();
    }
}
