package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

final class ImageUrlFunction
    extends AbstractUrlFunction
{
    public ImageUrlFunction()
    {
        super( "imageUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().imageUrl(), params ).toString();
    }
}
