package com.enonic.xp.core.impl.module;

import java.net.URL;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;

final class ModuleImpl
    implements Module
{
    protected ModuleKey moduleKey;

    protected ModuleVersion moduleVersion;

    protected String displayName;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected String systemVersion;

    protected Bundle bundle;

    protected ClassLoader classLoader;

    @Override
    public ModuleKey getKey()
    {
        return this.moduleKey;
    }

    @Override
    public ModuleVersion getVersion()
    {
        return moduleVersion;
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
        // TODO: Use X-System-Version header. VersionRange.
        return "5.1";
    }

    @Override
    public String getMinSystemVersion()
    {
        // TODO: Use X-System-Version header. VersionRange.
        return "5.0";
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
    public URL getResource( final String path )
    {
        if ( this.bundle.getState() != Bundle.ACTIVE )
        {
            return null;
        }
        return this.bundle.getResource( path );
    }

    @Override
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

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
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

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "moduleKey", moduleKey ).
            add( "displayName", displayName ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            omitNullValues().
            toString();
    }
}
