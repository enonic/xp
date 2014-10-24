package com.enonic.wem.api.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrincipalTest
{

    @Test
    public void testCreateUser()
    {
        final User user = User.newUser().
            login( "userlogin" ).
            displayName( "my user" ).
            userKey( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            build();

        assertEquals( "userlogin", user.getLogin() );
        assertEquals( "my user", user.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), user.getKey() );
        assertEquals( "user@email", user.getEmail() );

        final User userCopy = User.newUser( user ).build();
        assertEquals( "userlogin", userCopy.getLogin() );
        assertEquals( "my user", userCopy.getDisplayName() );
        assertEquals( false, userCopy.isDisabled() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), userCopy.getKey() );
        assertEquals( "user@email", userCopy.getEmail() );
    }

    @Test
    public void testCreateGroup()
    {
        final Group group = Group.newGroup().
            displayName( "my group" ).
            groupKey( PrincipalKey.ofGroup( new UserStoreKey( "myuserstore" ), "groupid" ) ).
            build();

        assertEquals( "my group", group.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:group:groupid" ), group.getKey() );

        final Group groupCopy = Group.newGroup( group ).build();
        assertEquals( "my group", groupCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:group:groupid" ), groupCopy.getKey() );
    }

    @Test
    public void testAnonymous()
    {
        final User anonymous = User.anonymous();

        assertTrue( anonymous.getKey().isAnonymous() );
        assertEquals( "anonymous", anonymous.getDisplayName() );
        assertEquals( PrincipalKey.ofAnonymous(), anonymous.getKey() );
    }

    @Test
    public void testCreateRole()
    {
        final Role role = Role.newRole().
            displayName( "my role" ).
            roleKey( PrincipalKey.ofRole( "administrators" ) ).
            build();

        assertEquals( "my role", role.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:role:administrators" ), role.getKey() );

        final Role roleCopy = Role.newRole( role ).build();
        assertEquals( "my role", roleCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:role:administrators" ), roleCopy.getKey() );
    }

}