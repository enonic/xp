package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AssetUrlParams;

public final class AssetUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.assetUrl( params );
    }
}
