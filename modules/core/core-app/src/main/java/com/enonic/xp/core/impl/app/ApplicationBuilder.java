package com.enonic.xp.core.impl.app;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

public final class ApplicationBuilder
{
    public final static String X_APPLICATION_URL = "X-Application-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    public final static String X_SOURCE_PATHS = "X-Source-Paths";

    private final ApplicationImpl2 app;

    private ApplicationBuilder()
    {
        this.app = new ApplicationImpl2();
    }

    public ApplicationBuilder key( final String key )
    {
        return key( ApplicationKey.from( key ) );
    }

    public ApplicationBuilder key( final ApplicationKey key )
    {
        this.app.key = key;
        return this;
    }

    public ApplicationBuilder version( final String version )
    {
        return version( Version.parseVersion( version ) );
    }

    public ApplicationBuilder version( final Version version )
    {
        this.app.version = version;
        return this;
    }

    public ApplicationBuilder systemVersion( final String systemVersion )
    {
        return version( Version.parseVersion( systemVersion ) );
    }

    public ApplicationBuilder systemVersion( final VersionRange systemVersion )
    {
        this.app.systemVersion = systemVersion;
        return this;
    }

    public ApplicationBuilder displayName( final String displayName )
    {
        this.app.displayName = displayName;
        return this;
    }

    public ApplicationBuilder bundle( final Bundle bundle )
    {
        this.app.bundle = bundle;
        return this;
    }

    public ApplicationBuilder url( final String url )
    {
        this.app.url = url;
        return this;
    }

    public ApplicationBuilder vendorName( final String vendorName )
    {
        this.app.vendorName = vendorName;
        return this;
    }

    public ApplicationBuilder vendorUrl( final String vendorUrl )
    {
        this.app.vendorUrl = vendorUrl;
        return this;
    }

    public ApplicationBuilder sourcePaths( final Iterable<String> sourcePaths )
    {
        this.app.sourcePaths = ImmutableList.copyOf( sourcePaths );
        return this;
    }

    public ApplicationBuilder urlResolver( final ApplicationUrlResolver urlResolver )
    {
        this.app.urlResolver = urlResolver;
        return this;
    }

    public ApplicationBuilder classLoader( final ClassLoader classLoader )
    {
        this.app.classLoader = classLoader;
        return this;
    }

    public Application build()
    {
        return this.app;
    }

    public static ApplicationBuilder create()
    {
        return new ApplicationBuilder();
    }

    public static ApplicationBuilder from( final Bundle bundle )
    {
        return create().
            bundle( bundle ).
            key( bundle.getSymbolicName() ).
            version( bundle.getVersion() ).
            displayName( getHeader( bundle, Constants.BUNDLE_NAME, bundle.getSymbolicName() ) ).
            url( getHeader( bundle, X_APPLICATION_URL, null ) ).
            vendorName( getHeader( bundle, X_VENDOR_NAME, null ) ).
            vendorUrl( getHeader( bundle, X_VENDOR_URL, null ) ).
            systemVersion( getHeader( bundle, X_SYSTEM_VERSION, null ) ).
            sourcePaths( split( getHeader( bundle, X_SOURCE_PATHS, "" ), ',' ) );
    }

    private static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    private static Iterable<String> split( final String str, final char seperator )
    {
        return Splitter.on( seperator ).trimResults().split( str );
    }
}
