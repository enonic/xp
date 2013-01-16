package com.enonic.wem.web.rest.rpc.system;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class GetSystemInfoRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetSystemInfoRpcHandler handler = new GetSystemInfoRpcHandler();
        return handler;
    }

    @Test
    @Ignore
    // TODO: Remove test on version number
    public void testRequest()
        throws Exception
    {
        testSuccess( "getSystemInfo_result.json" );
    }
}
