package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class DeleteAccountsRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteAccountsRpcHandler()
    {
        super("account_delete");
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        // TODO: Implement here. Remember that multiple keys can be deleted.
    }
}
