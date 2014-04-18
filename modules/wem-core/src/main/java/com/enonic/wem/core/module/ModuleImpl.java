package com.enonic.wem.core.module;

import java.net.URL;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleVersion;

final class ModuleImpl
    implements Module
{
    private final ModuleKey moduleKey;

    private final String displayName;

    private final String info;

    private final String url;

    private final String vendorName;

    private final String vendorUrl;

    private final Form config;

    public ModuleImpl( final ModuleBuilder builder )
    {
        Preconditions.checkNotNull( builder.moduleKey, "moduleKey must be specified" );

        this.moduleKey = builder.moduleKey;
        this.displayName = builder.displayName;
        this.info = builder.info;
        this.url = builder.url;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.config = builder.config == null ? null : builder.config.copy();
    }

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

    public String getInfo()
    {
        return info;
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
        return null;
    }

    @Override
    public Set<String> getResourcePaths()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "moduleKey", moduleKey ).
            add( "displayName", displayName ).
            add( "info", info ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            add( "config", config ).
            omitNullValues().
            toString();
    }
}
