package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

final class AssetUrlHandler
    extends AbstractUrlHandler
{
    public AssetUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.assetUrl( params );
    }

}
