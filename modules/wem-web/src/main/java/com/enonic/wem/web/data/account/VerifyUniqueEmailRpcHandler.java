package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class VerifyUniqueEmailRpcHandler
    extends AbstractDataRpcHandler
{
    public VerifyUniqueEmailRpcHandler()
    {
        super("account_verifyUniqueEmail");
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        // TODO: Implement here.
    }
}
