package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.RestServiceUrlParams;

public final class RestServiceUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final RestServiceUrlParams params = new RestServiceUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.restServiceUrl( params );
    }
}
