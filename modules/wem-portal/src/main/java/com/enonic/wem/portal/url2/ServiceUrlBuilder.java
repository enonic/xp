package com.enonic.wem.portal.url2;

import java.util.Map;

import static com.google.common.base.Strings.emptyToNull;

public final class ServiceUrlBuilder
    extends PortalUrlBuilder<ServiceUrlBuilder>
{
    private String serviceName;

    private String module;

    public ServiceUrlBuilder module( final String module )
    {
        this.module = emptyToNull( module );
        return this;
    }

    public ServiceUrlBuilder serviceName( final String serviceName )
    {
        this.serviceName = emptyToNull( serviceName );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Map<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, "_" );
        appendPart( url, "service" );
        appendPart( url, this.module );
        appendPart( url, this.serviceName );
    }
}
