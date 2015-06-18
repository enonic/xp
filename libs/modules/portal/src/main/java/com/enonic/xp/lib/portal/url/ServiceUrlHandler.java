package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

final class ServiceUrlHandler
    extends AbstractUrlHandler
{
    public ServiceUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.serviceUrl( params );
    }

}
