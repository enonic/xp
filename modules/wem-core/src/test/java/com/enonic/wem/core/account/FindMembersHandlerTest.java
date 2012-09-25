package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

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

public class FindMembersHandlerTest
    extends AbstractCommandHandlerTest
{
    private static final String USERSTORE_KEY = "12345";

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreDao userStoreDao;

    private FindMembersHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );

        handler = new FindMembersHandler();
        handler.setGroupDao( groupDao );
        handler.setUserStoreDao( userStoreDao );
    }

    @Test
    public void testFindMembersNotTransitive()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:devs" );
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        final GroupEntity groupMember1 = createGroup( "10001", groupAccount.getUserStore(), "group1" );
        final GroupEntity groupMember2 = createGroup( "10002", groupAccount.getUserStore(), "group2" );

        // TODO fix this test with a user member for JCR
//        final UserEntity user1 = createUser( "10003U", account.getUserStore(), "user1" );
//        final GroupEntity groupMemberUser = createGroup( "10003", groupAccount.getUserStore(), "user1" );
//        groupMemberUser.setType( GroupType.USER );
        final GroupEntity groupMemberRole = createGroup( "10004", groupAccount.getUserStore(), "role1" );
        Mockito.when( groupMemberRole.isBuiltIn() ).thenReturn( true );

        final GroupEntity group =
            createGroup( "12345", groupAccount.getUserStore(), groupAccount.getLocalName(), groupMember1, groupMember2, groupMemberRole );

        // exercise
        final FindMembers command = Commands.account().findMembers().key( groupAccount );
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
//        assertTrue( members.contains( AccountKey.user( "enonic:user1" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test
    public void testFindMembersTransitive()
        throws Exception
    {
        // TODO improve this test when using JCR for storage
        final AccountKey groupAccount = AccountKey.group( "enonic:devs" );
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        final GroupEntity groupMember1 = createGroup( "10001", groupAccount.getUserStore(), "group1" );
        final GroupEntity groupMember2 = createGroup( "10002", groupAccount.getUserStore(), "group2" );

        // TODO fix this test with a user member for JCR
//        final UserEntity user1 = createUser( "10003U", account.getUserStore(), "user1" );
//        final GroupEntity groupMemberUser = createGroup( "10003", groupAccount.getUserStore(), "user1" );
//        groupMemberUser.setType( GroupType.USER );
        final GroupEntity groupMemberRole = createGroup( "10004", groupAccount.getUserStore(), "role1" );
        Mockito.when( groupMemberRole.isBuiltIn() ).thenReturn( true );

        createGroup( "12345", groupAccount.getUserStore(), groupAccount.getLocalName(), groupMember1, groupMember2, groupMemberRole );

        // exercise
        final FindMembers command = Commands.account().findMembers().key( groupAccount ).includeTransitive();
        this.handler.handle( this.context, command );
        final AccountKeys members = command.getResult();

        // verify
        assertNotNull( members );
        assertEquals( 3, members.getSize() );
        assertTrue( members.contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( members.contains( AccountKey.group( "enonic:group2" ) ) );
//        assertTrue( members.contains( AccountKey.user( "enonic:user1" ) ) );
        assertTrue( members.contains( AccountKey.role( "enonic:role1" ) ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembersMissingAccount()
        throws Exception
    {
        final AccountKey groupAccount = AccountKey.group( "enonic:group1" );
        createUserStore( groupAccount.getUserStore(), USERSTORE_KEY );

        final FindMembers command = Commands.account().findMembers().key( groupAccount );
        this.handler.handle( this.context, command );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testFindMembersUserAccount()
        throws Exception
    {
        final AccountKey userAccount = AccountKey.user( "enonic:user1" );
        final FindMembers command = Commands.account().findMembers().key( userAccount );
        this.handler.handle( this.context, command );
    }

    private GroupEntity createGroup( final String key, final String userStore, final String name, final GroupEntity... members )
        throws Exception
    {
        final UserStoreEntity userStoreEntity = createUserStore( userStore, USERSTORE_KEY );
        final GroupEntity group = Mockito.mock( GroupEntity.class, Mockito.CALLS_REAL_METHODS );
        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( userStoreEntity );
        group.setName( name );
        group.setDescription( "Group " + name );
        group.setDeleted( false );

        final Set<GroupEntity> memberSet = new HashSet<GroupEntity>();
        memberSet.addAll( Arrays.asList( members ) );
        group.setMembers( memberSet );

        mockAddGroupToUserStore( userStoreEntity, group );
        return group;
    }

    private UserEntity createUser( final String key, final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore, USERSTORE_KEY ) );
        user.setName( name );
        user.setDisplayName( name + " User" );

        mockAddUserToDaoByQualifiedName( user );
        return user;
    }

    private void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        QualifiedUsername qualifiedName = user.getQualifiedName();
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ) ) ).thenReturn( user );
    }

    private void mockAddGroupToUserStore( final UserStoreEntity userStore, final GroupEntity group )
    {
        final List<GroupEntity> userStoreResults = new ArrayList<GroupEntity>();
        userStoreResults.add( group );
        Mockito.when( groupDao.findByUserStoreKeyAndGroupname( userStore.getKey(), group.getName(), false ) ).thenReturn(
            userStoreResults );
    }

    private UserStoreEntity createUserStore( final String name, final String userStoreKey )
    {
        final UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( name );
        userStore.setKey( new UserStoreKey( userStoreKey ) );

        Mockito.when( userStoreDao.findByName( name ) ).thenReturn( userStore );

        return userStore;
    }

}
