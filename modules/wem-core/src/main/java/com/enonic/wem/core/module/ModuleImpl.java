package com.enonic.wem.core.module;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeNames;

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

    private final ModuleVersion minSystemVersion;

    private final ModuleVersion maxSystemVersion;

    private final ModuleKeys moduleDependencies;

    private final ContentTypeNames contentTypeDependencies;

    public ModuleImpl( final ModuleBuilder builder )
    {
        Preconditions.checkNotNull( builder.moduleKey, "moduleKey must be specified" );

        this.moduleKey = builder.moduleKey;
        this.displayName = builder.displayName;
        this.info = builder.info;
        this.url = builder.url;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.minSystemVersion = builder.minSystemVersion;
        this.maxSystemVersion = builder.maxSystemVersion;
        this.moduleDependencies = ModuleKeys.from( builder.moduleDependencies );
        this.contentTypeDependencies = ContentTypeNames.from( builder.contentTypeDependencies );
        this.config = builder.config == null ? null : builder.config.copy();
    }

    public ModuleKey getKey()
    {
        return getModuleKey();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
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

    public ModuleVersion getMinSystemVersion()
    {
        return minSystemVersion;
    }

    public ModuleVersion getMaxSystemVersion()
    {
        return maxSystemVersion;
    }

    public ModuleKeys getModuleDependencies()
    {
        return moduleDependencies;
    }

    public ContentTypeNames getContentTypeDependencies()
    {
        return contentTypeDependencies;
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
            add( "minSystemVersion", minSystemVersion ).
            add( "maxSystemVersion", maxSystemVersion ).
            add( "moduleDependencies", moduleDependencies ).
            add( "contentTypeDependencies", contentTypeDependencies ).
            omitNullValues().
            toString();
    }
}
