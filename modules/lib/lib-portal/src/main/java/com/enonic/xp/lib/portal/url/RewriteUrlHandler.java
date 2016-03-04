package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.RewriteUrlParams;

public final class RewriteUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final RewriteUrlParams params = new RewriteUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.rewriteUrl( params );
    }
}
