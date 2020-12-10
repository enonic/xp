package com.enonic.xp.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincipalTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static final Clock CLOCK = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void testCreateUser()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( CLOCK ) ).
            build();

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
    public void testCreateGroup()
    {
        final Group group = Group.create().
            displayName( "my group" ).
            key( PrincipalKey.ofGroup( IdProviderKey.from( "myidprovider" ), "groupid" ) ).
            modifiedTime( Instant.now( CLOCK ) ).
            build();

        assertEquals( "my group", group.getDisplayName() );
        assertEquals( PrincipalKey.from( "group:myidprovider:groupid" ), group.getKey() );

        final Group groupCopy = Group.create( group ).build();
        assertEquals( "my group", groupCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "group:myidprovider:groupid" ), groupCopy.getKey() );
        assertEquals( "groupid", groupCopy.getName() );
        assertEquals( "group:myidprovider:groupid", groupCopy.toString() );
    }

    @Test
    public void testAnonymous()
    {
        assertTrue( User.ANONYMOUS.getKey().isAnonymous() );
        assertEquals( "anonymous", User.ANONYMOUS.getKey().getId() );
        assertEquals( "anonymous", User.ANONYMOUS.getName() );
        assertEquals( "Anonymous User", User.ANONYMOUS.getDisplayName() );
        assertEquals( "user:system:anonymous", User.ANONYMOUS.toString() );
    }

    @Test
    public void testCreateRole()
    {
        final Role role = Role.create().
            displayName( "my role" ).
            key( PrincipalKey.ofRole( "administrators" ) ).
            modifiedTime( Instant.now( CLOCK ) ).
            build();

        assertEquals( "my role", role.getDisplayName() );
        assertEquals( PrincipalKey.from( "role:administrators" ), role.getKey() );

        final Role roleCopy = Role.create( role ).build();
        assertEquals( "my role", roleCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "role:administrators" ), roleCopy.getKey() );
        assertEquals( "administrators", roleCopy.getName() );
        assertEquals( "role:administrators", roleCopy.toString() );
    }
}
