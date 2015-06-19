package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PageUrlParams;

public final class PageUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }
}
