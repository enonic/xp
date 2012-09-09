package com.enonic.wem.web.rest.rpc.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.service.account.UserIdGenerator;

@Component
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
            accountKey = AccountKey.user( userStore + ":" + suggestedUserName );
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
