package com.enonic.wem.web.data.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.core.account.FindAccountsHandler;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.AccountType;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;
import com.enonic.wem.web.rest2.resource.account.IsQualifiedUsername;

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

import static org.mockito.Mockito.doReturn;

public class FindAccountsRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private AccountSearchService accountSearchService;

    private FindAccountsHandler findAccountsHandler;

    public void setup()
    {
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        accountSearchService = Mockito.mock( AccountSearchService.class );

        findAccountsHandler = new FindAccountsHandler();
        findAccountsHandler.setUserDao( userDao );
        findAccountsHandler.setGroupDao( groupDao );
        findAccountsHandler.setUserStoreDao( userStoreDao );
        findAccountsHandler.setAccountSearchService( accountSearchService );
    }

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        setup();

        final FindAccountsRpcHandler handler = new FindAccountsRpcHandler();
        handler.setClient( getClient() );
        return handler;
    }

    @Test
    public void testRequest()
        throws Exception
    {
        final GroupEntity group = createGroup( "enonic", "group1" );
        final GroupEntity role = createRole( "enonic", "contributors" );
        final UserEntity user = createUser( "enonic", "user1" );

        final AccountSearchResults searchResults = new AccountSearchResults( 0, 10 );
        searchResults.add( new AccountKey( user.getKey().toString() ), AccountType.USER, 1 );
        searchResults.add( new AccountKey( role.getGroupKey().toString() ), AccountType.ROLE, 1 );
        searchResults.add( new AccountKey( group.getGroupKey().toString() ), AccountType.GROUP, 1 );
        doReturn( searchResults ).when( accountSearchService ).search( Matchers.<AccountSearchQuery>any() );

        mockCurrentContextHttpRequest();

        testSuccess( "findAccounts_param.json", "findAccounts_result.json" );
    }

    private Client getClient()
    {
        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( findAccountsHandler );
        standardClient.setInvoker( commandInvoker );
        return standardClient;
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = new MockHttpServletRequest();
        final ServletRequestAttributes attrs = new ServletRequestAttributes( req );
        RequestContextHolder.setRequestAttributes( attrs );
    }

    private GroupEntity createRole( final String userStore, final String name )
    {
        return createGroupOrRole( userStore, name, true );
    }

    private GroupEntity createGroup( final String userStore, final String name )

    {
        return createGroupOrRole( userStore, name, false );
    }

    private GroupEntity createGroupOrRole( final String userStore, final String name, final boolean isRole )

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
        user.setTimestamp( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );

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
