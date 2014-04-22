package com.enonic.wem.core.module;

import java.io.File;
import java.net.URL;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
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

    protected String displayName;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected Form config;

    protected File moduleDir;

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
        return moduleKey.getVersion();
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
    public URL getResource( final String path )
    {
        final File file = new File( this.moduleDir, path );
        if ( !file.isFile() )
        {
            return null;
        }

        try
        {
            return file.toURI().toURL();
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public Set<String> getResourcePaths()
    {
        final Set<String> set = Sets.newHashSet();
        findResourcePaths( set, this.moduleDir );
        return set;
    }

    private void findResourcePaths( final Set<String> set, final File file )
    {
        if ( file.isFile() )
        {
            set.add( file.toString().substring( this.moduleDir.toString().length() + 1 ) );
        }

        final File[] children = file.listFiles();
        if ( children == null )
        {
            return;
        }

        for ( final File child : children )
        {
            findResourcePaths( set, child );
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
