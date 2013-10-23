package com.enonic.wem.admin.rpc.account;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rest.service.account.UserIdGenerator;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;


public final class SuggestUserNameRpcHandler
    extends AbstractDataRpcHandler
{
    public SuggestUserNameRpcHandler()
    {
        super( "account_suggestUserName" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String userStore = context.param( "userStore" ).required().asString();
        final String firstName = context.param( "firstName" ).asString( "" );
        final String lastName = context.param( "lastName" ).asString( "" );

        final UserIdGenerator userIdGenerator = new UserIdGenerator( firstName, lastName );

        AccountKey accountKey;
        String suggestedUserName;
        do
        {
            suggestedUserName = userIdGenerator.nextUserName();
            accountKey = UserKey.from( userStore + ":" + suggestedUserName );
        }
        while ( userExists( accountKey ) );

        context.setResult( new SuggestUserNameJsonResult( suggestedUserName ) );
    }

    private boolean userExists( final AccountKey accountKey )
    {
        final Accounts result = this.client.execute( Commands.account().get().keys( AccountKeys.from( accountKey ) ) );
        return !result.isEmpty();
    }
}
