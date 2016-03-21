package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.GenerateUrlParams;

public final class UrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final GenerateUrlParams params = new GenerateUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.generateUrl( params );
    }
}
