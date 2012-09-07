package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountResult;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;
import com.enonic.wem.core.search.Facet;
import com.enonic.wem.core.search.FacetEntry;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class FindAccountsHandlerTest
{
    private Client client;

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private AccountSearchService accountSearchService;


    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        accountSearchService = Mockito.mock( AccountSearchService.class );

        final FindAccountsHandler findAccountsHandler = new FindAccountsHandler();
        findAccountsHandler.setUserDao( userDao );
        findAccountsHandler.setGroupDao( groupDao );
        findAccountsHandler.setUserStoreDao( userStoreDao );
        findAccountsHandler.setAccountSearchService( accountSearchService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( findAccountsHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }

    @Test
    public void testFindAccountsByQueryUsers()
        throws Exception
    {
        // setup
        final UserEntity user1 = createUser( "enonic", "user1" );
        final UserEntity user2 = createUser( "enonic", "user2" );
        final UserEntity user3 = createUser( "enonic", "user3" );
        createGroup( "enonic", "group1" );
        createGroup( "enonic", "group2" );
        createGroup( "enonic", "group3" );
        createRole( "enonic", "contributors" );
        createRole( "enonic", "administrators" );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 10 );
        searchResults.add( new AccountKey( user1.getKey().toString() ), com.enonic.wem.core.search.account.AccountType.USER, 1 );
        searchResults.add( new AccountKey( user2.getKey().toString() ), com.enonic.wem.core.search.account.AccountType.USER, 1 );
        searchResults.add( new AccountKey( user3.getKey().toString() ), com.enonic.wem.core.search.account.AccountType.USER, 1 );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query = new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.USER );

        AccountResult accountResult = client.execute( Commands.account().find().query( query ).includeImage() );

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );
        assertEquals( 10, accountResult.getTotalSize() );
    }

    @Test
    public void testFindAccountsByQueryGroupsRoles()
        throws Exception
    {
        // setup
        final UserEntity user1 = createUser( "enonic", "user1" );
        final UserEntity user2 = createUser( "enonic", "user2" );
        final UserEntity user3 = createUser( "enonic", "user3" );
        final GroupEntity group1 = createGroup( "enonic", "group1" );
        final GroupEntity group2 = createGroup( "enonic", "group2" );
        final GroupEntity group3 = createGroup( "enonic", "group3" );
        final GroupEntity role1 = createRole( "enonic", "contributors" );
        final GroupEntity role2 = createRole( "enonic", "administrators" );

        addMembers( group1, user1.getUserGroup(), user2.getUserGroup() );
        addMembers( group2, user3.getUserGroup() );
        addMembers( role1, user3.getUserGroup(), group1, role2 );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 7 );
        searchResults.add( new AccountKey( group1.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( group2.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( group3.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( role1.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.ROLE, 1 );
        searchResults.add( new AccountKey( role2.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.ROLE, 1 );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query =
            new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.GROUP, AccountType.ROLE );

        AccountResult accountResult = client.execute( Commands.account().find().query( query ).includeMembers() );

        // verify
        assertNotNull( accountResult );
        assertEquals( 5, accountResult.getSize() );
        assertEquals( 7, accountResult.getTotalSize() );
    }

    @Test
    public void testFindAccountsByQueryFacets()
        throws Exception
    {
        // setup
        final UserEntity user1 = createUser( "enonic", "user1" );
        final UserEntity user2 = createUser( "enonic", "user2" );
        final UserEntity user3 = createUser( "enonic", "user3" );
        final GroupEntity group1 = createGroup( "enonic", "group1" );
        final GroupEntity group2 = createGroup( "enonic", "group2" );
        final GroupEntity group3 = createGroup( "enonic", "group3" );
        final GroupEntity role1 = createRole( "enonic", "contributors" );
        final GroupEntity role2 = createRole( "enonic", "administrators" );

        addMembers( group1, user1.getUserGroup(), user2.getUserGroup() );
        addMembers( group2, user3.getUserGroup() );
        addMembers( role1, user3.getUserGroup(), group1, role2 );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 7 );
        searchResults.add( new AccountKey( group1.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( group2.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( group3.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.GROUP, 1 );
        searchResults.add( new AccountKey( role1.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.ROLE, 1 );
        searchResults.add( new AccountKey( role2.getGroupKey().toString() ), com.enonic.wem.core.search.account.AccountType.ROLE, 1 );
        final Facet facet = new Facet( "organization" );
        facet.addEntry( new FacetEntry( "Enonic", 2 ) );
        facet.addEntry( new FacetEntry( "Acme, inc.", 3 ) );
        facet.addEntry( new FacetEntry( "Foo Bars", 3 ) );
        searchResults.getFacets().addFacet( facet );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        // exercise
        final AccountQuery query =
            new AccountQuery().offset( 0 ).limit( 2 ).sortDesc( "userstore" ).types( AccountType.GROUP, AccountType.ROLE );

        AccountResult accountResult = client.execute( Commands.account().find().query( query ).includeMembers() );

        // verify
        assertNotNull( accountResult );
        assertEquals( 5, accountResult.getSize() );
        assertEquals( 7, accountResult.getTotalSize() );
        assertNotNull( accountResult.getFacets() );

        AccountFacet facetInResults = accountResult.getFacets().getFacet( "organization" );
        assertNotNull( facetInResults );
        assertEquals( 3, facetInResults.getEntries().size() );
        assertEquals( "organization",facetInResults.getName());
        assertEquals( 3, facetInResults.getEntries().size());
    }

    private void addMembers( final GroupEntity group, final GroupEntity... members )
    {
        final Set<GroupEntity> memberSet = Sets.newHashSet();
        Collections.addAll( memberSet, members );
        Mockito.when( group.getMembers( false ) ).thenReturn( memberSet );
    }

    private GroupEntity createRole( final String userStore, final String name )
        throws Exception
    {
        return createGroupOrRole( userStore, name, true );
    }

    private GroupEntity createGroup( final String userStore, final String name )
        throws Exception
    {
        return createGroupOrRole( userStore, name, false );
    }

    private GroupEntity createGroupOrRole( final String userStore, final String name, final boolean isRole )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        final GroupKey key = new GroupKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        group.setKey( key );
        group.setType( isRole ? GroupType.USERSTORE_ADMINS : GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );
        group.setMemberships( Sets.<GroupEntity>newHashSet() );

        final Set<GroupEntity> memberSet = Sets.newHashSet();
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        Mockito.when( groupDao.findByKey( key ) ).thenReturn( group );

        return group;
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    private UserEntity createUser( final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        final UserKey key = new UserKey( Integer.toString( Math.abs( name.hashCode() ) ) );

        user.setKey( key );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore ) );
        user.setName( name );
        user.setDisplayName( "User " + name );
        user.setDeleted( false );

        final QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( user.getQualifiedName() ).thenReturn( qualifiedName );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ) ) ).thenReturn( user );
        Mockito.when( userDao.findByKey( key.toString() ) ).thenReturn( user );

        final GroupEntity userGroup = createGroup( userStore, "G" + user.getKey().toString() );
        userGroup.setType( GroupType.USER );
        Mockito.when( user.getUserGroup() ).thenReturn( userGroup );
        doReturn( user ).when( userGroup ).getUser();

        return user;
    }

    private UserStoreEntity createUserStore( final String name )
    {
        final UserStoreEntity userStore = Mockito.mock( UserStoreEntity.class, Mockito.CALLS_REAL_METHODS );
        userStore.setName( name );
        final UserStoreKey userStoreKey = new UserStoreKey( Math.abs( name.hashCode() ) );
        userStore.setKey( userStoreKey );

        Mockito.when( userStoreDao.findByKey( userStoreKey ) ).thenReturn( userStore );
        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStore );

        return userStore;
    }
}
