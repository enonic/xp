package com.enonic.wem.web.rest.rpc.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class DeleteAccountsRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteAccountsRpcHandler()
    {
        super( "account_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] keys = context.param( "key" ).required().asStringArray();
        final AccountKeys accountKeys = AccountKeys.from( keys );

        int accountsDeleted = 0;
        for ( AccountKey accountKey : accountKeys )
        {
            final boolean accountDeleted = this.client.execute( Commands.account().delete().key( accountKey ) );
            if ( accountDeleted )
            {
                accountsDeleted++;
            }
        }
        context.setResult( new DeleteAccountsJsonResult( accountsDeleted ) );
    }
}
