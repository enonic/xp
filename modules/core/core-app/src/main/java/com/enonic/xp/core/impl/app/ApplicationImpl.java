package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.enonic.xp.app.ApplicationBundleUtils;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;

final class ApplicationImpl
    implements ApplicationAdaptor
{
    private final ApplicationKey key;

    private final VersionRange systemVersion;

    private final Bundle bundle;

    private final ApplicationUrlResolver urlResolver;

    private final ClassLoader classLoader;

    private volatile Configuration config;

    private final Set<String> capabilities;

    ApplicationImpl( final Bundle bundle, final ApplicationUrlResolver urlResolver, final ClassLoader classLoader,
                     final Configuration config )
    {
        this.bundle = bundle;
        this.key = ApplicationKey.from( bundle );
        this.systemVersion = ApplicationHelper.parseVersionRange( getHeader( ApplicationManifestConstants.X_SYSTEM_VERSION, null ) );
        this.urlResolver = urlResolver;
        this.classLoader = classLoader;
        this.config = config;
        this.capabilities = ApplicationHelper.getCapabilities( bundle );
    }

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        return this.bundle.getVersion();
    }

    @Override
    public String getDisplayName()
    {
        return getHeader( Constants.BUNDLE_NAME, this.getKey().toString() );
    }

    @Override
    public String getSystemVersion()
    {
        return this.systemVersion != null ? this.systemVersion.toString() : null;
    }

    @Override
    public String getMaxSystemVersion()
    {
        return this.systemVersion != null ? this.systemVersion.getRight().toString() : null;
    }

    @Override
    public String getMinSystemVersion()
    {
        return this.systemVersion != null ? this.systemVersion.getLeft().toString() : null;
    }

    @Override
    public boolean includesSystemVersion( final Version version )
    {
        return this.systemVersion == null || this.systemVersion.isEmpty() || this.systemVersion.includes( version );
    }

    @Override
    public String getUrl()
    {
        return getHeader( ApplicationManifestConstants.X_APPLICATION_URL, null );
    }

    @Override
    public String getVendorName()
    {
        return getHeader( ApplicationManifestConstants.X_VENDOR_NAME, null );
    }

    @Override
    public String getVendorUrl()
    {
        return getHeader( ApplicationManifestConstants.X_VENDOR_URL, null );
    }

    @Override
    public Bundle getBundle()
    {
        return this.bundle;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
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

    private String getHeader( final String name, final String defValue )
    {
        return ApplicationHelper.getHeader( this.bundle, name, defValue );
    }

    @Override
    public ApplicationUrlResolver getUrlResolver()
    {
        return urlResolver;
    }

    @Override
    public Configuration getConfig()
    {
        return this.config;
    }

    @Override
    public void setConfig( final Configuration config )
    {
        this.config = config;
    }

    @Override
    public Set<String> getCapabilities()
    {
        return this.capabilities;
    }

    @Override
    public boolean isSystem()
    {
        return ApplicationBundleUtils.isSystemApplication( this.bundle );
    }
}
