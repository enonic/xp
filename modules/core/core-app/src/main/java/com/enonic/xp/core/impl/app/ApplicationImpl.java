package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.google.common.annotations.Beta;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;

@Beta
final class ApplicationImpl
    implements Application
{
    public final static String X_APPLICATION_URL = "X-Application-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    public final static String X_SOURCE_PATHS = "X-Source-Paths";

    private final ApplicationKey applicationKey;

    private final Version version;

    private final String displayName;

    private final String url;

    private final String vendorName;

    private final String vendorUrl;

    private final String systemVersion;

    private final Bundle bundle;

    private final List<String> sourcePaths;

    public ApplicationImpl( final Bundle bundle )
    {
        this.bundle = bundle;
        this.applicationKey = ApplicationKey.from( bundle );
        this.version = this.bundle.getVersion();
        this.displayName = getHeader( this.bundle, Constants.BUNDLE_NAME, this.getKey().toString() );
        this.url = getHeader( this.bundle, X_APPLICATION_URL, null );
        this.vendorName = getHeader( this.bundle, X_VENDOR_NAME, null );
        this.vendorUrl = getHeader( this.bundle, X_VENDOR_URL, null );
        this.systemVersion = getHeader( this.bundle, X_SYSTEM_VERSION, null );
        this.sourcePaths = split( getHeader( this.bundle, X_SOURCE_PATHS, "" ), ',' );
    }

    @Override
    public ApplicationKey getKey()
    {
        return this.applicationKey;
    }

    @Override
    public Version getVersion()
    {
        return version;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getSystemVersion()
    {
        return this.systemVersion;
    }

    @Override
    public String getMaxSystemVersion()
    {
        String maxSystemVersion = null;
        if ( this.systemVersion != null )
        {
            try
            {
                maxSystemVersion = VersionRange.valueOf( this.systemVersion ).getRight().toString();
            }
            catch ( final Exception e )
            {
                //Nothing to do
            }
        }
        return maxSystemVersion;
    }

    @Override
    public String getMinSystemVersion()
    {
        String maxSystemVersion = null;
        if ( this.systemVersion != null )
        {
            try
            {
                maxSystemVersion = VersionRange.valueOf( this.systemVersion ).getLeft().toString();
            }
            catch ( final Exception e )
            {
                //Nothing to do
            }
        }
        return maxSystemVersion;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Override
    public String getVendorName()
    {
        return vendorName;
    }

    @Override
    public String getVendorUrl()
    {
        return vendorUrl;
    }

    @Override
    public Bundle getBundle()
    {
        return this.bundle;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return new BundleClassLoader( this.bundle );
    }

    @Override
    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( this.bundle.getLastModified() );
    }

    @Override
    public boolean isStarted()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    private static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    @Override
    public List<String> getSourcePaths()
    {
        return this.sourcePaths;
    }

    private static List<String> split( final String str, final char seperator )
    {
        return Lists.newArrayList( Splitter.on( seperator ).trimResults().split( str ) );
    }

    @Override
    public Set<String> getFiles()
    {
        return null;
    }

    @Override
    public URL resolveFile( final String path )
    {
        return null;
    }
}
