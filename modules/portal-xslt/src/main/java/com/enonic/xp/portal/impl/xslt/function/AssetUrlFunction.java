package com.enonic.xp.portal.impl.xslt.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AssetUrlParams;

final class AssetUrlFunction
    extends AbstractUrlFunction
{
    public AssetUrlFunction()
    {
        super( "assetUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final AssetUrlParams params = new AssetUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.assetUrl( params );
    }
}
