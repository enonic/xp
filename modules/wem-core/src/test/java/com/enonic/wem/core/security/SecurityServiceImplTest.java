package com.enonic.wem.core.security;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
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
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

        final Group group = Group.newGroup().groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).displayName( "Group A" ).build();
        securityService.createGroup( group );

        final Principals groups = securityService.getPrincipals( SYSTEM, PrincipalType.GROUP );
        final Principals users = securityService.getPrincipals( SYSTEM, PrincipalType.USER );
        assertEquals( "Group A", groups.getPrincipal( group.getKey() ).getDisplayName() );
        assertEquals( "User 1", users.getPrincipal( user.getKey() ).getDisplayName() );
    }

    @Test
    public void testAuthenticateByEmailPwd()
        throws Exception
    {
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

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
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

        final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
        authToken.setUsername( "user1" );
        authToken.setPassword( "123456" );
        authToken.setUserStore( SYSTEM );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );
        assertEquals( user.getKey(), authInfo.getUser().getKey() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAuthenticateUnsupportedToken()
        throws Exception
    {
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

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
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

        final User user2 = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user2" ) ).
            displayName( "User 2" ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
        securityService.createUser( user2 );

        final Principals users = securityService.getPrincipals( SYSTEM, PrincipalType.USER );
        assertEquals( 2, users.getSize() );
    }

    @Test
    public void testUpdateUser()
        throws Exception
    {
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

        final User userUpdate = User.newUser( user ).
            email( "u2@enonic.net" ).
            build();
        securityService.updateUser( userUpdate );

        final User updatedUser = securityService.getUser( user.getKey() );
        assertEquals( "u2@enonic.net", updatedUser.getEmail() );
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        final Group group = Group.newGroup().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        securityService.createGroup( group );

        final Group group2 = Group.newGroup().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupB" ) ).
            displayName( "Group B" ).
            build();
        securityService.createGroup( group2 );

        final Principals groups = securityService.getPrincipals( SYSTEM, PrincipalType.GROUP );
        assertEquals( 2, groups.getSize() );
    }

    @Test
    public void testUpdateGroup()
        throws Exception
    {
        final Group group = Group.newGroup().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        securityService.createGroup( group );

        final Group groupUpdate = Group.newGroup( group ).
            displayName( "___Group B___" ).
            build();
        securityService.updateGroup( groupUpdate );

        final Group updatedGroup = securityService.getGroup( group.getKey() );
        assertEquals( "___Group B___", updatedGroup.getDisplayName() );
    }

    @Test
    public void testQuery()
        throws Exception
    {
        final User user = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        securityService.createUser( user );

        final User user2 = User.newUser().
            userKey( PrincipalKey.ofUser( SYSTEM, "user2" ) ).
            displayName( "User 2" ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
        securityService.createUser( user2 );

        final Group group = Group.newGroup().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupA" ) ).
            displayName( "Group A" ).
            build();
        securityService.createGroup( group );

        final Group group2 = Group.newGroup().
            groupKey( PrincipalKey.ofGroup( SYSTEM, "groupB" ) ).
            displayName( "Group B" ).
            build();
        securityService.createGroup( group2 );

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