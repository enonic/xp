package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

final class PageUrlFunction
    extends AbstractUrlFunction
{
    public PageUrlFunction()
    {
        super( "pageUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().pageUrl(), params ).toString();
    }
}
