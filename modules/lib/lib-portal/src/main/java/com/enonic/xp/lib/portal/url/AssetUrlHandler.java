package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.AssetUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class AssetUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.assetUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("path", "application", "type", "params");
    }
}
