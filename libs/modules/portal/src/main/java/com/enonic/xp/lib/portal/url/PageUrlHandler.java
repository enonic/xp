package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

final class PageUrlHandler
    extends AbstractUrlHandler
{
    public PageUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }

}
