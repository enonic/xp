package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ComponentUrlParams;

public final class ComponentUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.componentUrl( params );
    }
}
