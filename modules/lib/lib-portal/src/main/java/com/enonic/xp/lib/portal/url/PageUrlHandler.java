package com.enonic.xp.lib.portal.url;

import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PageUrlParams;

public final class PageUrlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS = Set.of( "id", "path", "type", "params" );

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
