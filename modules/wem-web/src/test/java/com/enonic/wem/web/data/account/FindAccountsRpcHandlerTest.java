package com.enonic.wem.web.data.account;

import org.junit.Test;

import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;

public class FindAccountsRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final FindAccountsRpcHandler handler = new FindAccountsRpcHandler();
        return handler;
    }

    // @Test
    public void testRequest()
        throws Exception
    {
        testSuccess( "findAccounts_param.json", "findAccounts_result.json" );
    }
}
