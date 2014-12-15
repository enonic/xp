package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

final class GeneralUrlFunction
    extends AbstractUrlFunction
{
    public GeneralUrlFunction()
    {
        super( "url" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().generalUrl(), params ).toString();
    }
}
