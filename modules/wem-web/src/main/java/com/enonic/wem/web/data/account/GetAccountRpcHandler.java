package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.result.JsonErrorResult;
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
            AccountSelector selector = AccountSelectors.keys( accountKey );
            final AccountResult account = this.client.execute( Commands.account().find().selector( selector ).includeImage() );

            if ( account.getTotalSize() == 1 )
            {
                if ( accountKey.isUser() )
                {
                    AccountKeys membershipKeys = this.client.execute( Commands.account().findMemberships().key( accountKey ) );
                    AccountResult memberships =
                        this.client.execute( Commands.account().find().selector( AccountSelectors.keys( membershipKeys ) ) );
                    context.setResult( new GetAccountJsonResult( account.first(), null, memberships.getAll() ) );
                }
                else
                {
                    AccountKeys memberKeys = this.client.execute( Commands.account().findMembers().key( accountKey ) );
                    AccountResult members =
                        this.client.execute( Commands.account().find().selector( AccountSelectors.keys( memberKeys ) ) );
                    context.setResult( new GetAccountJsonResult( account.first(), members.getAll(), null ) );
                }
            }
            else
            {
                JsonErrorResult result = new JsonErrorResult();
                result.error( "1", ( account.getTotalSize() == 0 ? "No" : account.getTotalSize() ) +
                    " account(s) were found for key [" + key + "]" );
                context.setResult( result );
            }
        }
        catch ( IllegalArgumentException e )
        {
            JsonErrorResult result = new JsonErrorResult();
            result.error( "1", e.getMessage() );
            context.setResult( result );
        }
    }
}
