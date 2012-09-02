package com.enonic.wem.web.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

import com.enonic.cms.core.locale.LocaleService;

@Component
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

    @Autowired
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
