package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

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
