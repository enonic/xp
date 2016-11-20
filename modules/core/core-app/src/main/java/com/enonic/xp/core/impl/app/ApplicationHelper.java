package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.VersionRange;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class ApplicationHelper
{
    public final static String X_APPLICATION_URL = "X-Application-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    public final static String X_SOURCE_PATHS = "X-Source-Paths";

    public static final String SITE_XML = "site/site.xml";

    public static final String APPLICATION_XML = "application.xml";

    public static final String BUNDLE_TYPE_HEADER = "X-Bundle-Type";

    public static final String APPLICATION_BUNDLE_TYPE = "application";

    public static boolean isApplication( final Bundle bundle )
    {
        return ( getHeader( bundle, BUNDLE_TYPE_HEADER, "default" ).equals( APPLICATION_BUNDLE_TYPE ) ||
            bundle.getEntry( APPLICATION_XML ) != null || bundle.getEntry( SITE_XML ) != null );
    }

    public static boolean isApplication( final JarFile jarFile )
    {
        return hasApplicationHeader( jarFile ) || jarFile.getEntry( APPLICATION_XML ) != null || jarFile.getEntry( SITE_XML ) != null;
    }

    private static final boolean hasApplicationHeader( final JarFile jarFile )
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

        return getAttribute( manifest, BUNDLE_TYPE_HEADER, "default" ).equals( APPLICATION_BUNDLE_TYPE );
    }

    public static String getAttribute( final Manifest manifest, final String name, final String defValue )
    {
        if ( manifest == null )
        {
            return defValue;
        }

        return manifest.getMainAttributes().getValue( name ) != null ? manifest.getMainAttributes().getValue( name ) : defValue;
    }

    public static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    public static List<String> getSourcePaths( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_SOURCE_PATHS, "" );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return Lists.newArrayList();
        }
        return Lists.newArrayList( Splitter.on( ',' ).trimResults().split( value ) );
    }

    public static VersionRange parseVersionRange( final String value )
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

    public static <T> T callWithContext( Callable<T> runnable )
    {
        return getContext().callWith( runnable );
    }

    public static void runWithContext( Runnable runnable )
    {
        getContext().runWith( runnable );
    }

    private static Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build();
    }

}
