package com.enonic.wem.admin.rpc.account;


import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.command.Commands;


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
            final Accounts accounts =
                this.client.execute( Commands.account().get().keys( accountKeys ).includeImage().includeProfile().includeMembers() );

            if ( !accounts.isEmpty() )
            {
                final Account account = accounts.first();
                if ( accountKey.isUser() )
                {
                    AccountKeys membershipKeys = this.client.execute( Commands.account().findMemberships().key( accountKey ) );
                    Accounts memberships = this.client.execute( Commands.account().get().keys( membershipKeys ) );
                    context.setResult( new GetAccountJsonResult( account, null, memberships.getList() ) );
                }
                else
                {
                    final AccountKeys memberKeys = ( (NonUserAccount) account ).getMembers();
                    Accounts members = this.client.execute( Commands.account().get().keys( memberKeys ) );
                    context.setResult( new GetAccountJsonResult( account, members.getList(), null ) );
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
