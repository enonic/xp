package com.enonic.wem.core.module;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleVersion;

final class ModuleImpl
    implements Module
{
    protected ModuleKey moduleKey;

    protected ModuleVersion moduleVersion;

    protected String displayName;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected Form config;

    protected File moduleDir;

    protected Bundle bundle;

    public ModuleKey getKey()
    {
        return this.moduleKey;
    }

    public ModuleName getName()
    {
        return moduleKey.getName();
    }

    public ModuleVersion getVersion()
    {
        return moduleVersion;
    }

    public String getDisplayName()
    {
        return displayName;
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

    public Form getConfig()
    {
        return config;
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
        return Objects.toStringHelper( this ).
            add( "moduleKey", moduleKey ).
            add( "displayName", displayName ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            add( "config", config ).
            omitNullValues().
            toString();
    }
}
