package com.enonic.wem.admin.rpc.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.enonic.wem.admin.json.JsonSerializable;
import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;


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
        Map<AccountKey, AccountKeys> nodeMap = new HashMap<AccountKey, AccountKeys>();
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
            this.client.execute( Commands.account().get().keys( AccountKeys.from( nodeMap.keySet() ) ).includeImage() ).getList();
        Map<Account, List<Account>> result = new TreeMap<Account, List<Account>>( new GraphComparator( accountKey ) );
        for ( Map.Entry<AccountKey, AccountKeys> entry : nodeMap.entrySet() )
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

    private void generateMembersGraph( AccountKey accountKey, int level, Map<AccountKey, AccountKeys> graph )
    {
        if ( graph == null )
        {
            graph = new HashMap<AccountKey, AccountKeys>();
        }

        if ( !graph.containsKey( accountKey ) )
        {
            AccountKeys accountMembers;
            if ( accountKey.isUser() )
            {
                accountMembers = AccountKeys.empty();
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

    private void generateMembershipsGraph( AccountKey accountKey, int level, Map<AccountKey, AccountKeys> graph )
    {
        if ( graph == null )
        {
            graph = new HashMap<AccountKey, AccountKeys>();
        }
        AccountKeys accountMemberships = this.client.execute( Commands.account().findMemberships().key( accountKey ) );
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


    private class GraphComparator
        implements Comparator<Account>
    {
        private final AccountKey requestedKey;

        private GraphComparator( final AccountKey requestedKey )
        {
            this.requestedKey = requestedKey;
        }

        @Override
        public int compare( final Account one, final Account two )
        {
            if ( one == null && two == null )
            {
                return 0;
            }
            else if ( one == null || ( two != null && requestedKey.equals( two.getKey() ) ) )
            {
                return 1;
            }
            else if ( two == null || requestedKey.equals( one.getKey() ) )
            {
                return -1;
            }
            else
            {
                return one.getKey().toString().compareTo( two.getKey().toString() );
            }
        }

    }
}
