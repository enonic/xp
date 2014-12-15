package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

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
