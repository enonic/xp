package com.enonic.wem.core.account;

import java.util.List;
import java.util.Set;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;
import com.enonic.wem.api.account.query.AccountFacets;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.Facet;
import com.enonic.wem.core.search.FacetEntry;
import com.enonic.wem.core.search.Facets;
import com.enonic.wem.core.search.SearchSortOrder;
import com.enonic.wem.core.search.account.AccountIndexField;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;

import static com.enonic.wem.api.account.query.AccountQuery.Direction;

@Component
public final class FindAccountsHandler
    extends CommandHandler<FindAccounts>
{
    private AccountDao accountDao;

    private AccountSearchService accountSearchService;

    public FindAccountsHandler()
    {
        super( FindAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final FindAccounts command )
        throws Exception
    {
        final AccountQuery selector = command.getQuery();

        final AccountQueryHits result = findByQuery( context.getJcrSession(), selector );
        command.setResult( result );
    }

    private AccountQueryHits findByQuery( final Session session, final AccountQuery accountQuery )
        throws Exception
    {
        final AccountSearchQuery searchQuery = new AccountSearchQuery();
        searchQuery.from( accountQuery.getOffset() );
        searchQuery.count( accountQuery.getLimit() );
        searchQuery.query( accountQuery.getQuery() );
        searchQuery.userStores( accountQuery.getUserStores().toArray( new String[accountQuery.getUserStores().size()] ) );
        final Set<AccountType> accountTypes = accountQuery.getTypes();
        searchQuery.users( accountTypes.contains( AccountType.USER ) );
        searchQuery.groups( accountTypes.contains( AccountType.GROUP ) );
        searchQuery.roles( accountTypes.contains( AccountType.ROLE ) );
        searchQuery.sortField( AccountIndexField.parse( accountQuery.getSortField() ) );
        searchQuery.sortOrder( accountQuery.getSortDirection() == Direction.ASC ? SearchSortOrder.ASC : SearchSortOrder.DESC );
        searchQuery.email( accountQuery.getEmail() );

        final AccountSearchResults searchResults = accountSearchService.search( searchQuery );
        final List<AccountKey> accounts = getSearchResults( session, searchResults );
        final AccountFacets facets = getSearchFacets( searchResults );

        final AccountQueryHits accountResult = new AccountQueryHits( searchResults.getTotal(), AccountKeys.from( accounts ) );
        accountResult.setFacets( facets );
        return accountResult;
    }

    private List<AccountKey> getSearchResults( final Session session, final AccountSearchResults searchResults )
        throws Exception
    {
        final List<AccountKey> accounts = Lists.newArrayList();
        for ( AccountSearchHit hit : searchResults )
        {
            final AccountKey accountKey = AccountKey.from( hit.getKey().toString() );
            if ( accountDao.accountExists( session, accountKey ) )
            {
                accounts.add( accountKey );
            }
        }
        return accounts;
    }

    private AccountFacets getSearchFacets( final AccountSearchResults searchResults )
    {
        final AccountFacets accountFacets = new AccountFacets();
        final Facets searchFacets = searchResults.getFacets();
        for ( Facet searchFacet : searchFacets )
        {
            final AccountFacet accountFacet = getSearchFacets( searchFacet );
            accountFacets.addFacet( accountFacet );
        }
        return accountFacets;
    }

    private AccountFacet getSearchFacets( final Facet searchFacet )
    {
        final AccountFacet facet = new AccountFacet( searchFacet.getName() );
        for ( FacetEntry facetEntry : searchFacet )
        {
            facet.addEntry( new AccountFacetEntry( facetEntry.getTerm(), facetEntry.getCount() ) );
        }
        return facet;
    }

    @Autowired
    public void setAccountSearchService( final AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
