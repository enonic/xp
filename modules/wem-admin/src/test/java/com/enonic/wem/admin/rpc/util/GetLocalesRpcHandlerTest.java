package com.enonic.wem.admin.rpc.util;

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.core.locale.LocaleService;

public class GetLocalesRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private LocaleService localeService;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        this.localeService = Mockito.mock( LocaleService.class );
        final GetLocalesRpcHandler handler = new GetLocalesRpcHandler();
        handler.setLocaleService( this.localeService );
        return handler;
    }

    @Test
    public void testRequest()
        throws Exception
    {
        Mockito.when( localeService.getLocales() ).thenReturn( new Locale[]{Locale.ENGLISH, Locale.GERMAN} );
        testSuccess( "getLocales_result.json" );
    }
}
