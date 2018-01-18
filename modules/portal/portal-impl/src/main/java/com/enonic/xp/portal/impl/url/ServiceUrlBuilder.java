package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ServiceUrlBuilder
    extends GenericEndpointUrlBuilder<ServiceUrlParams>
{
    public ServiceUrlBuilder()
    {
        super( "service" );
    }

    private ApplicationKey getAplication()
    {
        return new ApplicationResolver().
            portalRequest( this.portalRequest ).
            application( this.params.getApplication() ).
            resolve();
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, getAplication().toString() );
        appendPart( url, this.params.getService() );
    }
}
