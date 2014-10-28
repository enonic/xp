package com.enonic.wem.core.security;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UpdateGroupParams;
import com.enonic.wem.api.security.UpdateUserParams;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationException;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;

import static org.junit.Assert.*;

public class SecurityServiceImplTest
{

    public static final UserStoreKey SYSTEM = UserStoreKey.system();

    private SecurityService securityService;

    @Before
    public final void setUp()
    {
        securityService = new SecurityServiceImpl();
    }

    @Test
    public void testGetUserStores()
        throws Exception
    {
        final UserStores userStores = securityService.getUserStores();
        assertNotNull( userStores.getUserStore( SYSTEM ) );
    }

    @Test
    public void testGetPrincipals()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user = securityService.createUser( createUser );

        final CreateGroupParams createGroup = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        final Group group = securityService.createGroup( createGroup );

        final Principals groups = securityService.getPrincipals( SYSTEM, PrincipalType.GROUP );
        final Principals users = securityService.getPrincipals( SYSTEM, PrincipalType.USER );
        assertEquals( "Group A", groups.getPrincipal( group.getKey() ).getDisplayName() );
        assertEquals( "User 1", users.getPrincipal( user.getKey() ).getDisplayName() );
    }

    @Test
    public void testAuthenticateByEmailPwd()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user = securityService.createUser( createUser );

        final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken();
        authToken.setEmail( "user1@enonic.com" );
        authToken.setPassword( "123456" );
        authToken.setUserStore( SYSTEM );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );
        assertEquals( user.getKey(), authInfo.getUser().getKey() );
    }

    @Test
    public void testAuthenticateByUsernamePwd()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user = securityService.createUser( createUser );

        final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
        authToken.setUsername( "user1" );
        authToken.setPassword( "123456" );
        authToken.setUserStore( SYSTEM );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );
        assertEquals( user.getKey(), authInfo.getUser().getKey() );
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateUnsupportedToken()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user = securityService.createUser( createUser );

        final CustomAuthenticationToken authToken = new CustomAuthenticationToken();
        authToken.setUserStore( SYSTEM );
        authToken.setPin( "123" );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );
        assertEquals( user.getKey(), authInfo.getUser().getKey() );
    }

    @Test
    public void testSetPassword()
        throws Exception
    {
        securityService.setPassword( PrincipalKey.ofUser( SYSTEM, "user1" ), "123456" );
    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        final CreateUserParams createUser1 = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            password( "123456" ).
            build();
        final User user1 = securityService.createUser( createUser1 );

        final CreateUserParams createUser2 = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user2" ) ).
            displayName( "User 2" ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
        final User user2 = securityService.createUser( createUser2 );

        final Principals users = securityService.getPrincipals( SYSTEM, PrincipalType.USER );
        assertEquals( 2, users.getSize() );
    }

    @Test
    public void testUpdateUser()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user = securityService.createUser( createUser );

        final UpdateUserParams updateUserParams = UpdateUserParams.create( user ).
            email( "u2@enonic.net" ).
            build();
        final User updateUserResult = securityService.updateUser( updateUserParams );

        final User updatedUser = securityService.getUser( user.getKey() ).get();
        assertEquals( "u2@enonic.net", updateUserResult.getEmail() );
        assertEquals( "u2@enonic.net", updatedUser.getEmail() );
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        final CreateGroupParams createGroup = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        final Group group = securityService.createGroup( createGroup );

        final CreateGroupParams createGroup2 = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupB" ) ).
            displayName( "Group B" ).
            build();
        final Group group2 = securityService.createGroup( createGroup2 );

        final Principals groups = securityService.getPrincipals( SYSTEM, PrincipalType.GROUP );
        assertEquals( 2, groups.getSize() );
    }

    @Test
    public void testUpdateGroup()
        throws Exception
    {
        final CreateGroupParams createGroup = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        final Group group = securityService.createGroup( createGroup );

        final UpdateGroupParams groupUpdate = UpdateGroupParams.create( group ).
            displayName( "___Group B___" ).
            build();
        final Group updatedGroupResult = securityService.updateGroup( groupUpdate );

        final Group updatedGroup = securityService.getGroup( group.getKey() ).get();
        assertEquals( "___Group B___", updatedGroupResult.getDisplayName() );
        assertEquals( "___Group B___", updatedGroup.getDisplayName() );
    }

    @Test
    public void testQuery()
        throws Exception
    {
        final CreateUserParams createUser1 = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final User user1 = securityService.createUser( createUser1 );

        final CreateUserParams createUser2 = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( SYSTEM, "user2" ) ).
            displayName( "User 2" ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
        final User user2 = securityService.createUser( createUser2 );

        final CreateGroupParams createGroup = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        final Group group = securityService.createGroup( createGroup );

        final CreateGroupParams createGroup2 = CreateGroupParams.create().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupB" ) ).
            displayName( "Group B" ).
            build();
        final Group group2 = securityService.createGroup( createGroup2 );

        final PrincipalQuery query = PrincipalQuery.newQuery().
            includeUsers().
            includeGroups().
            userStore( SYSTEM ).
            size( 3 ).
            build();

        final PrincipalQueryResult results = securityService.query( query );
        assertEquals( 4, results.getTotalSize() );
        assertEquals( 3, results.getPrincipals().getSize() );
    }

    @Test
    public void testAddRelationships()
        throws Exception
    {
        PrincipalKey user = PrincipalKey.ofUser( UserStoreKey.system(), "user" );
        PrincipalKey user2 = PrincipalKey.ofUser( UserStoreKey.system(), "user2" );
        PrincipalKey group = PrincipalKey.ofGroup( UserStoreKey.system(), "group" );
        PrincipalKey group2 = PrincipalKey.ofGroup( UserStoreKey.system(), "group2" );

        PrincipalRelationship fromGroupToUser = PrincipalRelationship.from( group ).to( user );
        PrincipalRelationship fromGroupToGroup2 = PrincipalRelationship.from( group ).to( group2 );
        PrincipalRelationship fromGroupToUser2 = PrincipalRelationship.from( group ).to( user2 );
        securityService.addRelationship( fromGroupToUser );
        securityService.addRelationship( fromGroupToGroup2 );
        securityService.addRelationship( fromGroupToUser2 );
        securityService.addRelationship( fromGroupToUser2 );

        PrincipalRelationships groupRelationships = securityService.getRelationships( group );
        assertEquals( 3, groupRelationships.getSize() );

        PrincipalRelationships group2Relationships = securityService.getRelationships( group2 );
        assertEquals( 0, group2Relationships.getSize() );
    }

    @Test
    public void testRemoveRelationship()
        throws Exception
    {
        PrincipalKey user = PrincipalKey.ofUser( UserStoreKey.system(), "user" );
        PrincipalKey user2 = PrincipalKey.ofUser( UserStoreKey.system(), "user2" );
        PrincipalKey group = PrincipalKey.ofGroup( UserStoreKey.system(), "group" );
        PrincipalKey group2 = PrincipalKey.ofGroup( UserStoreKey.system(), "group2" );

        PrincipalRelationship fromGroupToUser = PrincipalRelationship.from( group ).to( user );
        PrincipalRelationship fromGroupToGroup2 = PrincipalRelationship.from( group ).to( group2 );
        PrincipalRelationship fromGroupToUser2 = PrincipalRelationship.from( group ).to( user2 );
        securityService.addRelationship( fromGroupToUser );
        securityService.addRelationship( fromGroupToGroup2 );
        securityService.addRelationship( fromGroupToUser2 );

        securityService.removeRelationship( fromGroupToGroup2 );

        PrincipalRelationships groupRelationships = securityService.getRelationships( group );
        assertEquals( 2, groupRelationships.getSize() );
        assertTrue( groupRelationships.contains( fromGroupToUser ) );
        assertTrue( groupRelationships.contains( fromGroupToUser2 ) );
    }

    @Test
    public void testRemoveRelationships()
        throws Exception
    {
        PrincipalKey user = PrincipalKey.ofUser( UserStoreKey.system(), "user" );
        PrincipalKey user2 = PrincipalKey.ofUser( UserStoreKey.system(), "user2" );
        PrincipalKey group = PrincipalKey.ofGroup( UserStoreKey.system(), "group" );
        PrincipalKey group2 = PrincipalKey.ofGroup( UserStoreKey.system(), "group2" );

        PrincipalRelationship fromGroupToUser = PrincipalRelationship.from( group ).to( user );
        PrincipalRelationship fromGroupToGroup2 = PrincipalRelationship.from( group ).to( group2 );
        PrincipalRelationship fromGroupToUser2 = PrincipalRelationship.from( group ).to( user2 );
        securityService.addRelationship( fromGroupToUser );
        securityService.addRelationship( fromGroupToGroup2 );
        securityService.addRelationship( fromGroupToUser2 );

        securityService.removeRelationships( group );

        PrincipalRelationships groupRelationships = securityService.getRelationships( group );
        assertEquals( 0, groupRelationships.getSize() );
    }

    public class CustomAuthenticationToken
        extends AuthenticationToken
    {
        private String pin;

        public void setPin( final String pin )
        {
            this.pin = pin;
        }
    }

}