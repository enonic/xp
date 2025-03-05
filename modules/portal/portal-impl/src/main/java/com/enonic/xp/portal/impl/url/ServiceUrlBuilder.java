package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ServiceUrlBuilder
    extends GenericEndpointUrlBuilder<ServiceUrlParams>
{
    ServiceUrlBuilder()
    {
        super( "service" );
    }

    private ApplicationKey getApplication()
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
        UrlBuilderHelper.appendPart( url, getApplication().toString() );
        UrlBuilderHelper.appendPart( url, this.params.getService() );
    }
}
