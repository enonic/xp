package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

final class AssetUrlFunction
    extends AbstractUrlFunction
{
    public AssetUrlFunction()
    {
        super( "assetUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().assetUrl(), params ).toString();
    }
}
