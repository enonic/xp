package com.enonic.wem.core.account;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;
import com.enonic.wem.api.account.query.AccountFacets;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.command.account.FindAccounts;
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

import com.enonic.cms.core.security.QualifiedName;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static com.enonic.wem.api.account.query.AccountQuery.Direction;

@Component
public final class FindAccountsHandler
    extends CommandHandler<FindAccounts>
{
    private GroupDao groupDao;

    private UserDao userDao;

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

        final AccountQueryHits result = findByQuery( selector );
        command.setResult( result );
    }

    private AccountQueryHits findByQuery( final AccountQuery accountQuery )
    {
        final AccountSearchQuery searchQuery = new AccountSearchQuery();
        searchQuery.setFrom( accountQuery.getOffset() );
        searchQuery.setCount( accountQuery.getLimit() );
        searchQuery.setQuery( accountQuery.getQuery() );
        searchQuery.setUserStores( setToArray( accountQuery.getUserStores() ) );
        final Set<AccountType> accountTypes = accountQuery.getTypes();
        searchQuery.setUsers( accountTypes.contains( AccountType.USER ) );
        searchQuery.setGroups( accountTypes.contains( AccountType.GROUP ) );
        searchQuery.setRoles( accountTypes.contains( AccountType.ROLE ) );
        searchQuery.setSortField( AccountIndexField.parse( accountQuery.getSortField() ) );
        searchQuery.setSortOrder( accountQuery.getSortDirection() == Direction.ASC ? SearchSortOrder.ASC : SearchSortOrder.DESC );
        searchQuery.setEmail( accountQuery.getEmail() );

        final AccountSearchResults searchResults = accountSearchService.search( searchQuery );
        final List<AccountKey> accounts = getSearchResults( searchResults );
        final AccountFacets facets = getSearchFacets( searchResults );

        final AccountQueryHits accountResult = new AccountQueryHits( searchResults.getTotal(), AccountKeys.from( accounts ) );
        accountResult.setFacets( facets );
        return accountResult;
    }

    private List<AccountKey> getSearchResults( final AccountSearchResults searchResults )
    {
        final List<AccountKey> accounts = Lists.newArrayList();
        for ( AccountSearchHit hit : searchResults )
        {
            final String hibernateKey = hit.getKey().toString();
            switch ( hit.getAccountType() )
            {
                case USER:
                    final UserEntity user = userDao.findByKey( hibernateKey );
                    if ( user != null )
                    {
                        accounts.add( createUserKey( user ) );
                    }
                    break;

                case GROUP:
                    final GroupEntity group = groupDao.findByKey( new GroupKey( hibernateKey ) );
                    if ( group != null )
                    {
                        accounts.add( createGroupKey( group ) );
                    }
                    break;

                case ROLE:
                    final GroupEntity role = groupDao.findByKey( new GroupKey( hibernateKey ) );
                    if ( role != null )
                    {
                        accounts.add( createRoleKey( role ) );
                    }
                    break;
            }
        }
        return accounts;
    }

    private AccountKey createUserKey( final UserEntity entity )
    {
        return AccountKey.user( qualifiedName( entity.getQualifiedName() ) );
    }

    private AccountKey createGroupKey( final GroupEntity entity )
    {
        return AccountKey.group( qualifiedName( entity.getQualifiedName() ) );
    }

    private AccountKey createRoleKey( final GroupEntity entity )
    {
        return AccountKey.role( qualifiedName( entity.getQualifiedName() ) );
    }

    private String qualifiedName( QualifiedName qualifiedName )
    {
        String qName = qualifiedName.toString().replace( '\\', ':' );
        if ( !qName.contains( ":" ) )
        {
            qName = "system:" + qName;
        }

        return qName;
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

    private String[] setToArray( final Set<String> values )
    {
        final String[] array = new String[values.size()];
        values.toArray( array );
        return array;
    }

    @Autowired
    public void setAccountSearchService( final AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
