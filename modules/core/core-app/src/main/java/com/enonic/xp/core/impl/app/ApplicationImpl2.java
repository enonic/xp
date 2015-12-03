package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

final class ApplicationImpl2
    implements Application
{
    protected ApplicationKey key;

    protected Version version;

    protected String displayName;

    protected VersionRange systemVersion;

    protected Bundle bundle;

    protected Instant modifiedTime;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected List<String> sourcePaths;

    protected ApplicationUrlResolver urlResolver;

    protected ClassLoader classLoader;

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        return this.version;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
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
    public String getUrl()
    {
        return this.url;
    }

    @Override
    public String getVendorName()
    {
        return this.vendorName;
    }

    @Override
    public String getVendorUrl()
    {
        return this.vendorUrl;
    }

    @Override
    public Bundle getBundle()
    {
        return this.bundle;
    }

    @Override
    public Instant getModifiedTime()
    {
        return this.modifiedTime;
    }

    @Override
    public List<String> getSourcePaths()
    {
        return this.sourcePaths;
    }

    @Override
    public boolean isStarted()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    @Override
    public Set<String> getFiles()
    {
        return this.urlResolver.findFiles();
    }

    @Override
    public URL resolveFile( final String path )
    {
        return this.urlResolver.findUrl( path );
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }
}
