package com.enonic.xp.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Strings.emptyToNull;

public final class ServiceUrlBuilder
    extends PortalUrlBuilder<ServiceUrlBuilder>
{
    private String service;

    private String module;

    public final ServiceUrlBuilder module( final String value )
    {
        this.module = emptyToNull( value );
        return this;
    }

    public final ServiceUrlBuilder module( final ModuleKey value )
    {
        return module( value != null ? value.toString() : null );
    }

    public ServiceUrlBuilder service( final String value )
    {
        this.service = emptyToNull( value );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, "_" );
        appendPart( url, "service" );
        appendPart( url, this.module );
        appendPart( url, this.service );
    }
}
