package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

final class ComponentUrlHandler
    extends AbstractUrlHandler
{
    public ComponentUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.componentUrl( params );
    }

}
