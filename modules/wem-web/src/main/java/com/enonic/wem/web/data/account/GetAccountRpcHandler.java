package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class GetAccountRpcHandler
    extends AbstractDataRpcHandler
{
    public GetAccountRpcHandler()
    {
        super("account_get");
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        // TODO: Implement here. This should return members/memberships like old service.
    }
}
