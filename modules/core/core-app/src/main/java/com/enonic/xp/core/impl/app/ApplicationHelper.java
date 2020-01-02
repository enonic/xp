package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.VersionRange;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class ApplicationHelper
{
    final static String X_APPLICATION_URL = "X-Application-Url";

    final static String X_VENDOR_NAME = "X-Vendor-Name";

    final static String X_VENDOR_URL = "X-Vendor-Url";

    final static String X_SYSTEM_VERSION = "X-System-Version";

    final static String X_SOURCE_PATHS = "X-Source-Paths";

    final static String X_CAPABILITY = "X-Capability";

    static final String X_BUNDLE_TYPE = "X-Bundle-Type";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String SITE_XML = "site/site.xml";

    private static final String APPLICATION_XML = "application.xml";

    public static boolean isApplication( final Bundle bundle )
    {
        return getHeader( bundle, X_BUNDLE_TYPE, "" ).equals( APPLICATION_BUNDLE_TYPE ) || isSystemApplication( bundle ) ||
            bundle.getEntry( APPLICATION_XML ) != null || bundle.getEntry( SITE_XML ) != null;
    }

    static boolean isApplication( final JarFile jarFile )
    {
        return hasApplicationHeader( jarFile ) || jarFile.getEntry( APPLICATION_XML ) != null || jarFile.getEntry( SITE_XML ) != null;
    }

    public static boolean isSystemApplication( final Bundle bundle )
    {
        return getHeader( bundle, X_BUNDLE_TYPE, "" ).equals( SYSTEM_BUNDLE_TYPE );
    }

    private static boolean hasApplicationHeader( final JarFile jarFile )
    {
        final Manifest manifest;
        try
        {
            manifest = jarFile.getManifest();
        }
        catch ( IOException e )
        {
            return false;
        }

        final String value = getAttribute( manifest, X_BUNDLE_TYPE, "" );
        return value.equals( APPLICATION_BUNDLE_TYPE ) || value.equals( SYSTEM_BUNDLE_TYPE );
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

    public static <T> T runAsAdmin( final Callable<T> callable )
    {
        return ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).
            authInfo( ApplicationConstants.APPLICATION_SU_AUTH_INFO ).
            build().
            callWith( callable );
    }

    public static void runAsAdmin( final Runnable runnable )
    {
        ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).
            authInfo( ApplicationConstants.APPLICATION_SU_AUTH_INFO ).
            build().
            runWith( runnable );
    }

    static <T> T callWithContext( Callable<T> runnable )
    {
        return getContext().callWith( runnable );
    }

    static void runWithContext( Runnable runnable )
    {
        getContext().runWith( runnable );
    }

    private static Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build();
    }
}
