package com.enonic.xp.security;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrincipalTest
{
    @Test
    void testCreateUser()
    {
        final User user = User.create()
            .login( "userlogin" )
            .displayName( "my user" )
            .key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) )
            .email( "user@email" )
            .modifiedTime( Instant.ofEpochSecond( 0 ) )
            .build();

        assertEquals( "userlogin", user.getLogin() );
        assertEquals( "my user", user.getDisplayName() );
        assertEquals( PrincipalKey.from( "user:myidprovider:userid" ), user.getKey() );
        assertEquals( "user@email", user.getEmail() );

        final User userCopy = User.create( user ).build();
        assertEquals( "userlogin", userCopy.getLogin() );
        assertEquals( "my user", userCopy.getDisplayName() );
        assertFalse( userCopy.isDisabled() );
        assertEquals( PrincipalKey.from( "user:myidprovider:userid" ), userCopy.getKey() );
        assertEquals( "user@email", userCopy.getEmail() );
        assertEquals( "userid", userCopy.getName() );
        assertEquals( "user:myidprovider:userid", userCopy.toString() );
    }

    @Test
    void testCreateGroup()
    {
        final Group group = Group.create()
            .displayName( "my group" )
            .key( PrincipalKey.ofGroup( IdProviderKey.from( "myidprovider" ), "groupid" ) )
            .modifiedTime( Instant.ofEpochSecond( 0 ) )
            .build();

        assertEquals( "my group", group.getDisplayName() );
        assertEquals( PrincipalKey.from( "group:myidprovider:groupid" ), group.getKey() );

        final Group groupCopy = Group.create( group ).build();
        assertEquals( "my group", groupCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "group:myidprovider:groupid" ), groupCopy.getKey() );
        assertEquals( "groupid", groupCopy.getName() );
        assertEquals( "group:myidprovider:groupid", groupCopy.toString() );
    }

    @Test
    void testAnonymous()
    {
        assertTrue( User.anonymous().getKey().isAnonymous() );
        assertEquals( "anonymous", User.anonymous().getKey().getId() );
        assertEquals( "anonymous", User.anonymous().getName() );
        assertEquals( "Anonymous User", User.anonymous().getDisplayName() );
        assertEquals( "user:system:anonymous", User.anonymous().toString() );
    }

    @Test
    void testCreateRole()
    {
        final Role role = Role.create()
            .displayName( "my role" )
            .key( PrincipalKey.ofRole( "administrators" ) )
            .modifiedTime( Instant.ofEpochSecond( 0 ) )
            .build();

        assertEquals( "my role", role.getDisplayName() );
        assertEquals( PrincipalKey.from( "role:administrators" ), role.getKey() );

        final Role roleCopy = Role.create( role ).build();
        assertEquals( "my role", roleCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "role:administrators" ), roleCopy.getKey() );
        assertEquals( "administrators", roleCopy.getName() );
        assertEquals( "role:administrators", roleCopy.toString() );
    }
}
