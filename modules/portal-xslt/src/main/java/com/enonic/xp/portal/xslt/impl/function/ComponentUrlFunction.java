package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

final class ComponentUrlFunction
    extends AbstractUrlFunction
{
    public ComponentUrlFunction()
    {
        super( "componentUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().componentUrl(), params ).toString();
    }
}
