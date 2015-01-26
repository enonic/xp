package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ServiceUrlParams;

final class ServiceUrlFunction
    extends AbstractUrlFunction
{
    public ServiceUrlFunction()
    {
        super( "serviceUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final ServiceUrlParams params = new ServiceUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.serviceUrl( params );
    }
}
