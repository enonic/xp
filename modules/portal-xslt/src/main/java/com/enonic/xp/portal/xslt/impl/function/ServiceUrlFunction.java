package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

final class ServiceUrlFunction
    extends AbstractUrlFunction
{
    public ServiceUrlFunction()
    {
        super( "serviceUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().serviceUrl(), params ).toString();
    }
}
