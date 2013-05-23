package com.enonic.wem.admin.rest.rpc.util;

import javax.inject.Inject;

import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.core.locale.LocaleService;


public final class GetLocalesRpcHandler
    extends AbstractDataRpcHandler
{
    private LocaleService localeService;

    public GetLocalesRpcHandler()
    {
        super( "util_getLocales" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final LocaleJsonResult result = new LocaleJsonResult( this.localeService.getLocales() );
        context.setResult( result );
    }

    @Inject
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
