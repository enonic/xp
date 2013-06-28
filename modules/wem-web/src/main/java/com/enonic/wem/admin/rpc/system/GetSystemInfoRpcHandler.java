package com.enonic.wem.admin.rpc.system;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;


public final class GetSystemInfoRpcHandler
    extends AbstractDataRpcHandler
{

    public GetSystemInfoRpcHandler()
    {
        super( "system_getSystemInfo" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final SystemInfoJsonResult result = new SystemInfoJsonResult();
        context.setResult( result );
    }
}
