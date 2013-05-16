package com.enonic.wem.core.account;

import java.util.Set;

import javax.inject.Inject;

import org.elasticsearch.common.collect.Sets;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.account.AccountSearchHit;
import com.enonic.wem.core.index.account.AccountSearchQuery;
import com.enonic.wem.core.index.account.AccountSearchResults;
import com.enonic.wem.core.index.account.AccountSearchService;


public final class FindMembershipsHandler
    extends CommandHandler<FindMemberships>
{
    private AccountDao accountDao;

    private AccountSearchService accountSearchService;

    public FindMembershipsHandler()
    {
        super( FindMemberships.class );
    }

    @Override
    public void handle( final CommandContext context, final FindMemberships command )
        throws Exception
    {
        final AccountKey account = command.getKey();
        final boolean userExists = accountDao.accountExists( account, context.getJcrSession() );
        if ( !userExists )
        {
            throw new AccountNotFoundException( account );
        }

        final Set<AccountKey> memberships = Sets.newHashSet();
        findMemberships( AccountKeys.from( account ), memberships, command.isIncludeTransitive() );
        command.setResult( AccountKeys.from( memberships ) );
    }

    private void findMemberships( final AccountKeys accounts, final Set<AccountKey> memberships, final boolean transitive )
    {
        final AccountSearchQuery query = new AccountSearchQuery().membershipsFor( accounts );
        final AccountSearchResults searchResults = accountSearchService.search( query );

        final Set<AccountKey> addedMemberships = Sets.newHashSet();
        for ( AccountSearchHit searchHit : searchResults )
        {
            final AccountKey membership = searchHit.getKey();
            if ( memberships.add( membership ) )
            {
                addedMemberships.add( membership );
            }
        }

        if ( transitive && !addedMemberships.isEmpty() )
        {
            findMemberships( AccountKeys.from( addedMemberships ), memberships, transitive );
        }
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Inject
    public void setAccountSearchService( final AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }
}
