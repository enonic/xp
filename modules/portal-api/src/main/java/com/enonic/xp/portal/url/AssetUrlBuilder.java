package com.enonic.xp.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Strings.emptyToNull;

public final class AssetUrlBuilder
    extends PortalUrlBuilder<AssetUrlBuilder>
{
    private String path;

    private String module;

    public AssetUrlBuilder path( final String value )
    {
        this.path = emptyToNull( value );
        return this;
    }

    public final AssetUrlBuilder module( final String value )
    {
        this.module = emptyToNull( value );
        return this;
    }

    public final AssetUrlBuilder module( final ModuleKey value )
    {
        return module( value != null ? value.toString() : null );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, "_" );
        appendPart( url, "public" );
        appendPart( url, this.module );
        appendPart( url, this.path );
    }
}
