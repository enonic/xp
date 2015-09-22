package com.enonic.xp.app;

import java.time.Instant;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

@Beta
public class Application
{
    public final static String X_APPLICATION_URL = "X-Application-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    private static final String SITE_XML = "site/site.xml";

    private ApplicationKey applicationKey;

    private Version version;

    private String displayName;

    private String url;

    private String vendorName;

    private String vendorUrl;

    private String systemVersion;

    private Bundle bundle;

    private Application( final Bundle bundle )
    {
        this.bundle = bundle;
        this.applicationKey = ApplicationKey.from( bundle );
        this.version = this.bundle.getVersion();
        this.displayName = getHeader( this.bundle, Constants.BUNDLE_NAME, this.getKey().toString() );
        this.url = getHeader( this.bundle, X_APPLICATION_URL, null );
        this.vendorName = getHeader( this.bundle, X_VENDOR_NAME, null );
        this.vendorUrl = getHeader( this.bundle, X_VENDOR_URL, null );
        this.systemVersion = getHeader( this.bundle, X_SYSTEM_VERSION, null );
    }

    public static Application from( final Bundle bundle )
    {
        Application application = new Application( bundle );
        return application;
    }

    public ApplicationKey getKey()
    {
        return this.applicationKey;
    }

    public Version getVersion()
    {
        return version;
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
        String maxSystemVersion = null;
        if ( this.systemVersion != null )
        {
            try
            {
                maxSystemVersion = new VersionRange( this.systemVersion ).getRight().toString();
            }
            catch ( Exception e )
            {
                //Nothing to do
            }
        }
        return maxSystemVersion;
    }

    public String getMinSystemVersion()
    {
        String maxSystemVersion = null;
        if ( this.systemVersion != null )
        {
            try
            {
                maxSystemVersion = new VersionRange( this.systemVersion ).getLeft().toString();
            }
            catch ( Exception e )
            {
                //Nothing to do
            }
        }
        return maxSystemVersion;
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

    public Instant getModifiedTime()
    {
        return Instant.ofEpochMilli( this.bundle.getLastModified() );
    }

    public boolean isStarted()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    public void checkIfStarted()
    {
        if ( isStarted() )
        {
            return;
        }

        throw new ApplicationNotStartedException( this.applicationKey );
    }

    public boolean isApplication()
    {
        return true;
    }

    public boolean isSystem()
    {
        return !isApplication();
    }

    public static boolean isApplication( final Bundle bundle )
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
