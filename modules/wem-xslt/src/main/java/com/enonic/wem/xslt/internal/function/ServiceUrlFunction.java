package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

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
