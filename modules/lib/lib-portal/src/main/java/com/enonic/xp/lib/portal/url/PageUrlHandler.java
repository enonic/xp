package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.PageUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class PageUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("id", "path", "type", "params");
    }
}
