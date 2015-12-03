package com.enonic.xp.core.impl.app;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.VersionRange;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public final class ApplicationHelper
{
    public final static String X_APPLICATION_URL = "X-Application-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    public final static String X_SOURCE_PATHS = "X-Source-Paths";

    private static final String SITE_XML = "site/site.xml";

    public static boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getEntry( SITE_XML ) != null );
    }

    public static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    public static List<String> getSourcePaths( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_SOURCE_PATHS, "" );
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
}
