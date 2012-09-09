package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.json.rpc.JsonRpcContext;

@Component
public final class VerifyUniqueEmailRpcHandler
    extends AbstractDataRpcHandler
{
    public VerifyUniqueEmailRpcHandler()
    {
        super( "account_verifyUniqueEmail" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String userStore = context.param( "userStore" ).required().asString();
        final String email = context.param( "email" ).required().asString();

        final AccountQuery selector = new AccountQuery().email( email ).userStores( userStore );
        final AccountQueryHits result = this.client.execute( Commands.account().find().query( selector ) );

        if ( result.isEmpty() )
        {
            context.setResult( new VerifyUniqueEmailJsonResult( false ) );
        }
        else
        {
            context.setResult( new VerifyUniqueEmailJsonResult( true, result.getKeys().getFirst().toString() ) );
        }
    }
}
