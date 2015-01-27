package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ServiceUrlBuilder
    extends PortalUrlBuilder<ServiceUrlParams>
{
    private ModuleKey getModule()
    {
        return new ModuleResolver().
            context( this.context ).
            module( this.params.getModule() ).
            resolve();
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.context.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "service" );
        appendPart( url, getModule().toString() );
        appendPart( url, this.params.getService() );
    }
}
