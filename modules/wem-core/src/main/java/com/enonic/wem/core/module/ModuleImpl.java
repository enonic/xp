package com.enonic.wem.core.module;

import java.net.URL;
import java.util.Set;

import com.google.common.base.Objects;

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
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            add( "config", config ).
            omitNullValues().
            toString();
    }
}
