package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.GenerateUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class UrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final GenerateUrlParams params = new GenerateUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.generateUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("path", "type", "params");
    }
}
