package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.xp.portal.url.AssetUrlParams;

final class AssetUrlBuilder
    extends PortalUrlBuilder<AssetUrlParams>
{
    private ModuleKey getModule()
    {
        return choose( this.params.getModule(), this.context.getModule() );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.context.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "asset" );
        appendPart( url, getModule().toString() );
        appendPart( url, this.params.getPath() );
    }
}
