package com.enonic.wem.web.rest.rpc.system;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
