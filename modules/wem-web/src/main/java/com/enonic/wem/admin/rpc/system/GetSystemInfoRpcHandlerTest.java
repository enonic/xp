package com.enonic.wem.admin.rpc.system;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;

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
