package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class GetAccountRpcHandler
    extends AbstractDataRpcHandler
{
    public GetAccountRpcHandler()
    {
        super( "account_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        try
        {
            AccountKey accountKey = AccountKey.from( key );
            AccountKeys accountKeys = AccountKeys.from( accountKey );
            final Accounts account = this.client.execute( Commands.account().get().keys( accountKeys ).includeImage() );

            if ( !account.isEmpty() )
            {
                if ( accountKey.isUser() )
                {
                    AccountKeys membershipKeys = this.client.execute( Commands.account().findMemberships().key( accountKey ) );
                    Accounts memberships = this.client.execute( Commands.account().get().keys( membershipKeys ) );
                    context.setResult( new GetAccountJsonResult( account.getFirst(), null, memberships.getList() ) );
                }
                else
                {
                    AccountKeys memberKeys = this.client.execute( Commands.account().findMembers().key( accountKey ) );
                    Accounts members = this.client.execute( Commands.account().get().keys( memberKeys ) );
                    context.setResult( new GetAccountJsonResult( account.getFirst(), members.getList(), null ) );
                }
            }
            else
            {
                JsonErrorResult result = new JsonErrorResult( "No account(s) were found for key [" + key + "]" );
                context.setResult( result );
            }
        }
        catch ( IllegalArgumentException e )
        {
            JsonErrorResult result = new JsonErrorResult( e.getMessage() );
            context.setResult( result );
        }
    }
}
