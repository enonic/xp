package com.enonic.wem.api.security.auth;

import org.junit.Test;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;

import static org.junit.Assert.*;

public class AuthenticationInfoTest
{
    @Test
    public void testWithoutPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            build();

        final AuthenticationInfo info = AuthenticationInfo.create().user( user ).build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), info.getUser().getKey() );
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
            build();

        final UserStoreKey userStore = new UserStoreKey( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( userStore, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "myStore:group:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "system:role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principal( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), info.getUser().getKey() );
        assertEquals( 4, info.getPrincipals().getSize() );
        assertTrue( info.getPrincipals().contains( PrincipalKey.from( "myuserstore:user:userid" ) ) );
        assertTrue( info.getPrincipals().contains( group1 ) );
        assertTrue( info.getPrincipals().contains( group2 ) );
        assertTrue( info.getPrincipals().contains( role1 ) );
        assertFalse( info.hasRole( "userid" ) );
        assertFalse( info.hasRole( "group1" ) );
        assertFalse( info.hasRole( "group2" ) );
        assertTrue( info.hasRole( "administrators" ) );
    }
}
