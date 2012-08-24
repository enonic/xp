package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.EditableRoleAccount;
import com.enonic.wem.api.account.result.AccountFacet;
import com.enonic.wem.api.account.result.AccountFacets;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountKeySelector;
import com.enonic.wem.api.account.selector.AccountQuery;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.api.exception.SystemException;
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
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class FindAccountsHandler
    extends CommandHandler<FindAccounts>
{
    private UserStoreDao userStoreDao;

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
        final boolean includeMembers = command.isIncludeMembers();
        final boolean includePhoto = command.isIncludePhoto();
        final AccountSelector selector = command.getSelector();

        final AccountResult result = findBySelector( selector, includeMembers, includePhoto );

        command.setResult( result );
    }

    private AccountResult findBySelector( final AccountSelector selector, final boolean includeMembers, final boolean includePhoto )
    {
        if ( selector instanceof AccountKeySelector )
        {
            return findByKeySelector( (AccountKeySelector) selector, includeMembers, includePhoto );
        }
        else if ( selector instanceof AccountQuery )
        {
            return findByQuery( (AccountQuery) selector, includeMembers, includePhoto );
        }
        else
        {
            throw new SystemException( "Account selector of type {0} is not supported", selector.getClass().getName() );
        }
    }

    private AccountResult findByKeySelector( final AccountKeySelector accountKeySelector, final boolean includeMembers,
                                             final boolean includePhoto )
    {
        final AccountKeySet keys = accountKeySelector.getKeys();
        final List<Account> accounts = fetchAccounts( keys, includeMembers, includePhoto );
        final AccountResultImpl accountResult = new AccountResultImpl();
        accountResult.setAccounts( accounts );
        accountResult.setTotalSize( accounts.size() );
        return accountResult;
    }

    private AccountResult findByQuery( final AccountQuery accountQuery, final boolean includeMembers, final boolean includePhoto )
    {
        final AccountSearchQuery searchQuery = new AccountSearchQuery();
        searchQuery.setFrom( accountQuery.getOffset() );
        searchQuery.setCount( accountQuery.getLimit() );
        searchQuery.setQuery( accountQuery.getQuery() );
        searchQuery.setOrganizations( setToArray( accountQuery.getOrganizations() ) );
        searchQuery.setUserStores( setToArray( accountQuery.getUserStores() ) );
        final Set<AccountType> accountTypes = accountQuery.getTypes();
        searchQuery.setUsers( accountTypes.contains( AccountType.USER ) );
        searchQuery.setGroups( accountTypes.contains( AccountType.GROUP ) );
        searchQuery.setRoles( accountTypes.contains( AccountType.ROLE ) );
        searchQuery.setSortField( AccountIndexField.parse( accountQuery.getSortField() ) );
        searchQuery.setSortOrder(
            accountQuery.getSortDirection() == AccountQuery.Direction.ASC ? SearchSortOrder.ASC : SearchSortOrder.DESC );

        final AccountSearchResults searchResults = accountSearchService.search( searchQuery );
        final List<Account> accounts = getSearchResults( searchResults, includeMembers, includePhoto );
        final AccountFacets facets = getSearchFacets( searchResults );

        final AccountResultImpl accountResult = new AccountResultImpl();
        accountResult.setAccounts( accounts );
        accountResult.setTotalSize( searchResults.getTotal() );
        accountResult.setFacets( facets );
        return accountResult;
    }

    private List<Account> getSearchResults( final AccountSearchResults searchResults, final boolean includeMembers,
                                            final boolean includePhoto )
    {
        final List<Account> accounts = new ArrayList<Account>();
        for ( AccountSearchHit hit : searchResults )
        {
            final String hibernateKey = hit.getKey().toString();
            switch ( hit.getAccountType() )
            {
                case USER:
                    final UserEntity user = userDao.findByKey( hibernateKey );
                    if ( user != null )
                    {
                        accounts.add( buildUserAccount( user, includePhoto ) );
                    }
                    break;

                case GROUP:
                    final GroupEntity group = groupDao.findByKey( new GroupKey( hibernateKey ) );
                    if ( group != null )
                    {
                        accounts.add( buildGroupAccount( group, includeMembers ) );
                    }
                    break;

                case ROLE:
                    final GroupEntity role = groupDao.findByKey( new GroupKey( hibernateKey ) );
                    if ( role != null )
                    {
                        accounts.add( buildRoleAccount( role, includeMembers ) );
                    }
                    break;
            }
        }
        return accounts;
    }

    private UserAccount buildUserAccount( final UserEntity user, final boolean includePhoto )
    {
        final EditableUserAccountImpl userAccount = new EditableUserAccountImpl();
        final AccountKey key = AccountKey.user( qualifiedName( user.getQualifiedName() ) );
        userAccount.setKey( key );
        userAccount.setDisplayName( user.getDisplayName() );
        userAccount.setEmail( user.getEmail() );
        userAccount.setLastLoginTime( DateTime.now() ); // TODO fix when login-time is stored in backend
        userAccount.setCreatedTime( user.getTimestamp() ); // TODO fix when created-time is stored in backend
        userAccount.setModifiedTime( user.getTimestamp() );
        userAccount.setDeleted( user.isDeleted() );
        userAccount.setEditable( true ); // TODO evaluate if account is editable in the current context
        if ( includePhoto )
        {
            userAccount.setPhoto( user.getPhoto() );
        }
        return userAccount;
    }

    private EditableGroupAccountImpl buildGroupAccount( final GroupEntity groupEntity, final boolean includeMembers )
    {
        final EditableGroupAccountImpl group = new EditableGroupAccountImpl();
        buildNonUserAccount( group, groupEntity, includeMembers );
        return group;
    }

    private EditableRoleAccount buildRoleAccount( final GroupEntity groupEntity, final boolean includeMembers )
    {
        final EditableRoleAccountImpl role = new EditableRoleAccountImpl();
        buildNonUserAccount( role, groupEntity, includeMembers );
        return role;
    }

    private void buildNonUserAccount( final EditableNonUserAccountImpl nonUser, final GroupEntity groupEntity,
                                      final boolean includeMembers )
    {
        nonUser.setDisplayName( groupEntity.getDescription() );
        nonUser.setCreatedTime( DateTime.now() ); // TODO fix when created-time is stored in backend
        nonUser.setModifiedTime( DateTime.now() ); // TODO fix when modified-time is stored in backend
        nonUser.setDeleted( groupEntity.isDeleted() );
        nonUser.setEditable( true ); // TODO evaluate if account is editable in the current context
        AccountKey key;
        if ( groupEntity.isBuiltIn() )
        {
            key = AccountKey.role( qualifiedName( groupEntity.getQualifiedName() ) );
        }
        else
        {
            key = AccountKey.group( qualifiedName( groupEntity.getQualifiedName() ) );
        }
        nonUser.setKey( key );

        if ( includeMembers )
        {
            final AccountKeySet accountMembers = buildAccountMembers( groupEntity );
            nonUser.setMembers( accountMembers );
        }
    }

    private AccountKeySet buildAccountMembers( final GroupEntity groupEntity )
    {
        final Set<GroupEntity> members = groupEntity.getMembers( false );
        if ( ( members == null ) || members.isEmpty() )
        {
            return AccountKeySet.empty();
        }

        final Set<AccountKey> keys = Sets.newHashSet();
        for ( GroupEntity member : members )
        {
            keys.add( memberToAccountKey( member ) );
        }
        return AccountKeySet.from( keys );
    }

    private AccountKey memberToAccountKey( final GroupEntity groupEntity )
    {
        if ( groupEntity.getType() == GroupType.USER )
        {
            return AccountKey.user( qualifiedName( groupEntity.getUser().getQualifiedName() ) );
        }
        else if ( groupEntity.isBuiltIn() )
        {
            return AccountKey.role( qualifiedName( groupEntity.getQualifiedName() ) );
        }
        else
        {
            return AccountKey.group( qualifiedName( groupEntity.getQualifiedName() ) );
        }
    }

    private String qualifiedName( QualifiedName qualifiedName )
    {
        return qualifiedName.toString().replace( '\\', ':' );
    }

    private List<Account> fetchAccounts( final AccountKeySet keys, final boolean includeMembers, final boolean includePhoto )
    {
        final List<Account> accounts = new ArrayList<Account>();
        for ( AccountKey key : keys )
        {
            switch ( key.getType() )
            {
                case USER:
                    final UserEntity user = findUserEntity( key );
                    if ( user != null )
                    {
                        accounts.add( buildUserAccount( user, includePhoto ) );
                    }
                    break;

                case GROUP:
                    final GroupEntity group = findGroupEntity( key );
                    if ( group != null )
                    {
                        accounts.add( buildGroupAccount( group, includeMembers ) );
                    }
                    break;

                case ROLE:
                    final GroupEntity role = findGroupEntity( key );
                    if ( role != null )
                    {
                        accounts.add( buildRoleAccount( role, includeMembers ) );
                    }
                    break;
            }
        }
        return accounts;
    }

    private UserEntity findUserEntity( final AccountKey accountKey )
    {
        final QualifiedUsername qualifiedUserName = new QualifiedUsername( accountKey.getUserStore(), accountKey.getLocalName() );
        return userDao.findByQualifiedUsername( qualifiedUserName );
    }

    private GroupEntity findGroupEntity( final AccountKey accountKey )
    {
        final UserStoreKey userStoreKey = getUserStoreEntityKey( accountKey.getUserStore() );
        if ( userStoreKey == null )
        {
            return null;
        }
        final List<GroupEntity> memberAsGroup = groupDao.findByUserStoreKeyAndGroupname( userStoreKey, accountKey.getLocalName(), false );
        if ( memberAsGroup == null || memberAsGroup.isEmpty() )
        {
            return null;
        }
        return memberAsGroup.get( 0 );
    }

    private UserStoreKey getUserStoreEntityKey( final String userStoreName )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( userStoreName );
        return userStoreEntity == null ? null : userStoreEntity.getKey();
    }

    private AccountFacets getSearchFacets( final AccountSearchResults searchResults )
    {
        final AccountFacetsImpl accountFacets = new AccountFacetsImpl();
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
        final List<AccountFacet.Entry> entries = Lists.newArrayList();
        for ( FacetEntry facetEntry : searchFacet )
        {
            entries.add( new EntryImpl( facetEntry.getTerm(), facetEntry.getCount() ) );
        }
        return new AccountFacetImpl( searchFacet.getName(), entries );
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

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }
}
