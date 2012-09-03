package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class CreateOrUpdateAccountRpcHandler
    extends AbstractDataRpcHandler
{
    public CreateOrUpdateAccountRpcHandler()
    {
        super("account_createOrUpdate");
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        // TODO: Implement here.
    }
}
