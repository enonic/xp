package com.enonic.xp.core.security.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.PrincipalKeys;
import com.enonic.xp.core.security.User;
import com.enonic.xp.core.security.UserStoreKey;
import com.enonic.xp.core.security.auth.AuthenticationInfo;

import static org.junit.Assert.*;

public class AuthenticationInfoTest
{

    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void testWithoutPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final AuthenticationInfo info = AuthenticationInfo.create().user( user ).build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "user:myuserstore:userid" ), info.getUser().getKey() );
        assertEquals( 1, info.getPrincipals().getSize() );
        assertEquals( user.getKey(), info.getPrincipals().first() );
        assertFalse( info.hasRole( "userid" ) );
    }

    @Test
    public void testWithPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final UserStoreKey userStore = new UserStoreKey( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( userStore, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "group:myStore:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principals( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "user:myuserstore:userid" ), info.getUser().getKey() );
        assertEquals( 4, info.getPrincipals().getSize() );
        assertTrue( info.getPrincipals().contains( PrincipalKey.from( "user:myuserstore:userid" ) ) );
        assertTrue( info.getPrincipals().contains( group1 ) );
        assertTrue( info.getPrincipals().contains( group2 ) );
        assertTrue( info.getPrincipals().contains( role1 ) );
        assertFalse( info.hasRole( "userid" ) );
        assertFalse( info.hasRole( "group1" ) );
        assertFalse( info.hasRole( "group2" ) );
        assertTrue( info.hasRole( "administrators" ) );
    }
}
