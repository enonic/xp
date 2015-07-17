package com.enonic.xp.app;

import java.net.URL;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.xp.module.ModuleNotStartedException;

@Beta
public class Application
{
    public final static String X_MODULE_URL = "X-Module-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    private static final String SITE_XML = "app/site.xml";

    private ApplicationKey applicationKey;

    private Version moduleVersion;

    private String displayName;

    private String url;

    private String vendorName;

    private String vendorUrl;

    private String systemVersion;

    private Bundle bundle;

    private Application( final Bundle bundle ) {
        this.bundle = bundle;
        this.applicationKey = ApplicationKey.from( bundle );
        this.moduleVersion = this.bundle.getVersion();
        this.displayName = getHeader( this.bundle, Constants.BUNDLE_NAME, this.getKey().toString() );
        this.url = getHeader( this.bundle, X_MODULE_URL, null );
        this.vendorName = getHeader( this.bundle, X_VENDOR_NAME, null );
        this.vendorUrl = getHeader( this.bundle, X_VENDOR_URL, null );
        this.systemVersion = getHeader( this.bundle, X_SYSTEM_VERSION, null );
    }

    public static Application from( final Bundle bundle ) {
        Application application = new Application( bundle );
        return application;
    }

    public ApplicationKey getKey()
    {
        return this.applicationKey;
    }

    public Version getVersion()
    {
        return moduleVersion;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getSystemVersion()
    {
        return this.systemVersion;
    }

    public String getMaxSystemVersion()
    {
        // TODO: Use X-System-Version header. VersionRange.
        return "5.1";
    }

    public String getMinSystemVersion()
    {
        // TODO: Use X-System-Version header. VersionRange.
        return "5.0";
    }

    public String getUrl()
    {
        return url;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public String getVendorUrl()
    {
        return vendorUrl;
    }

    public Bundle getBundle()
    {
        return this.bundle;
    }

    public Set<String> getResourcePaths()
    {
        if ( this.bundle.getState() != Bundle.ACTIVE )
        {
            return Sets.newHashSet();
        }
        final Set<String> set = Sets.newHashSet();
        findResourcePaths( set, this.bundle, "/" );
        return set;
    }

    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( this.bundle.getLastModified() );
    }

    public boolean isStarted()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    private void findResourcePaths( final Set<String> set, final Bundle bundle, final String parentPath )
    {
        final Enumeration<URL> paths = bundle.findEntries( parentPath, "*", true );
        if ( paths == null )
        {
            return;
        }
        while ( paths.hasMoreElements() )
        {
            final URL path = paths.nextElement();
            set.add( path.getPath().replaceFirst( "^/", "" ) );
        }
    }

    public void checkIfStarted()
    {
        if ( isStarted() )
        {
            return;
        }

        throw new ModuleNotStartedException( this.applicationKey );
    }

    public boolean isApplication()
    {
        return true;
    }

    public boolean isSystem()
    {
        return !isApplication();
    }

    public static boolean isModule( final Bundle bundle )
    {
        return ( bundle.getEntry( SITE_XML ) != null );
    }

    private static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "applicationKey", applicationKey ).
            add( "displayName", displayName ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            omitNullValues().
            toString();
    }
}
