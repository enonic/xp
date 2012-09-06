package com.enonic.wem.web.data.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class GetAccountGraphRpcHandler
    extends AbstractDataRpcHandler
{
    public GetAccountGraphRpcHandler()
    {
        super( "account_getGraph" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        final AccountKey accountKey = AccountKey.from( key );
        Map<Account, List<Account>> result = generateAccountGraph( accountKey );
        final JsonSerializable json = new GetAccountGraphJsonResult( true, result );
        context.setResult( json );
    }

    private Map<Account, List<Account>> generateAccountGraph( AccountKey accountKey )
    {
        Map<AccountKey, AccountKeySet> nodeMap = new HashMap<AccountKey, AccountKeySet>();
        // We build full graph currently, this will be changed in future
        if ( accountKey.isUser() )
        {
            generateMembershipsGraph( accountKey, -1, nodeMap );
        }
        else if ( accountKey.isGroup() || accountKey.isRole() )
        {
            generateMembersGraph( accountKey, -1, nodeMap );
        }
        List<Account> accounts =
            this.client.execute( Commands.account().find().selector( AccountSelectors.keys( nodeMap.keySet() ) ) ).getAll();
        Map<Account, List<Account>> result = new HashMap<Account, List<Account>>();
        for ( Map.Entry<AccountKey, AccountKeySet> entry : nodeMap.entrySet() )
        {
            Account key = findAccount( entry.getKey(), accounts );
            List<Account> value = findAccounts( entry.getValue().getSet(), accounts );
            result.put( key, value );
        }
        return result;
    }

    private List<Account> findAccounts( Collection<AccountKey> accountKeys, Collection<Account> accountList )
    {
        List<Account> result = new ArrayList<Account>();
        for ( Account account : accountList )
        {
            if ( accountKeys.contains( account.getKey() ) )
            {
                result.add( account );
            }
        }
        return result;
    }

    private Account findAccount( AccountKey accountKey, Collection<Account> accountList )
    {
        for ( Account account : accountList )
        {
            if ( accountKey.equals( account.getKey() ) )
            {
                return account;
            }
        }
        return null;
    }

    private void generateMembersGraph( AccountKey accountKey, int level, Map<AccountKey, AccountKeySet> graph )
    {
        if ( graph == null )
        {
            graph = new HashMap<AccountKey, AccountKeySet>();
        }

        if ( !graph.containsKey( accountKey ) )
        {
            AccountKeySet accountMembers;
            if ( accountKey.isUser() )
            {
                accountMembers = AccountKeySet.empty();
            }
            else
            {
                accountMembers = this.client.execute( Commands.account().findMembers().key( accountKey ) );
            }
            graph.put( accountKey, accountMembers );
            if ( level != 0 )
            {
                for ( AccountKey memberKey : accountMembers )
                {
                    generateMembersGraph( memberKey, level - 1, graph );
                }
            }
        }
    }

    private void generateMembershipsGraph( AccountKey accountKey, int level, Map<AccountKey, AccountKeySet> graph )
    {
        if ( graph == null )
        {
            graph = new HashMap<AccountKey, AccountKeySet>();
        }
        AccountKeySet accountMemberships = this.client.execute( Commands.account().findMemberships().key( accountKey ) );
        if ( !graph.containsKey( accountKey ) )
        {
            graph.put( accountKey, accountMemberships );
            if ( level != 0 )
            {
                for ( AccountKey membershipKey : accountMemberships )
                {
                    generateMembershipsGraph( membershipKey, level - 1, graph );
                }
            }
        }

    }
}
