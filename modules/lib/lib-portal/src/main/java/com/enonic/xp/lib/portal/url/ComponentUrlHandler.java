package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.ComponentUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class ComponentUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.componentUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("id", "path", "component", "type", "params");
    }
}
