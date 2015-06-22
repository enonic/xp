package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

public final class ServiceUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.serviceUrl( params );
    }
}
