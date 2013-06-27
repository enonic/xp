package com.enonic.wem.admin.rpc.account;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.Commands;


public final class FindAccountsRpcHandler
    extends AbstractDataRpcHandler
{
    public FindAccountsRpcHandler()
    {
        super( "account_find" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        if ( context.param( "key" ).isNull() )
        {
            final AccountQuery selector = new AccountQuery( context.param( "query" ).asString( "" ) );
            selector.offset( context.param( "start" ).asInteger( 0 ) );
            selector.limit( context.param( "limit" ).asInteger( 10 ) );
            selector.userStores( context.param( "userstores" ).asStringArray() );
            selector.sort( context.param( "sort" ).asString( "" ), "ASC".equalsIgnoreCase( context.param( "dir" ).asString( "ASC" ) ) );

            if ( !context.param( "types" ).isNull() )
            {
                selector.types( getAccountTypes( context ) );
            }

            final AccountQueryHits hits = this.client.execute( Commands.account().find().query( selector ) );
            final Accounts accounts = this.client.execute( Commands.account().get().keys( hits.getKeys() ).includeImage() );
            context.setResult( new FindAccountsJsonResult( hits, accounts ) );
        }
        else
        {
            AccountKeys keys = AccountKeys.from( context.param( "key" ).asStringArray() );
            final Accounts accounts = this.client.execute( Commands.account().get().keys( keys ).includeImage() );
            context.setResult( new FindAccountsJsonResult( accounts ) );
        }
    }

    private AccountType[] getAccountTypes( final JsonRpcContext context )
    {
        if ( context.param( "types" ).isNull() )
        {
            return null;
        }

        final Set<AccountType> set = Sets.newHashSet();
        final String[] types = context.param( "types" ).asStringArray();
        for ( final String type : types )
        {
            set.add( AccountType.valueOf( type.toUpperCase() ) );
        }

        return set.toArray( new AccountType[set.size()] );
    }
}
