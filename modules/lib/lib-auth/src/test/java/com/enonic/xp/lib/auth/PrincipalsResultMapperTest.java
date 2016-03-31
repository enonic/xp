package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.testing.JsonAssert;

public class PrincipalsResultMapperTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void testUsersSerialized()
        throws Exception
    {
        final PrincipalsResultMapper principalsResultMapper = new PrincipalsResultMapper( createUserPrincipals(), 10 );
        JsonAssert.assertJson( getClass(), "userPrincipals", principalsResultMapper );
    }

    @Test
    public void testGroupsSerialized()
        throws Exception
    {

        final Principal group1 = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group1" ) ).
            displayName( "Group1" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description1" ).
            build();

        final Principal group2 = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group2" ) ).
            displayName( "Group2" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description2" ).
            build();

        final PrincipalsResultMapper principalsResultMapper = new PrincipalsResultMapper( Principals.from( group1, group2 ), 5 );
        JsonAssert.assertJson( getClass(), "groupPrincipals", principalsResultMapper );
    }

    @Test
    public void testRolesSerialized()
        throws Exception
    {

        final Principal role1 = Role.create().
            key( PrincipalKey.ofRole( "Role 1" ) ).
            displayName( "Role 1 Display Name" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description1" ).
            build();

        final Principal role2 = Role.create().
            key( PrincipalKey.ofRole( "Role 2" ) ).
            displayName( "Role 2 Display Name" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description2" ).
            build();

        final PrincipalsResultMapper principalsResultMapper = new PrincipalsResultMapper( Principals.from( role1, role2 ), 3 );
        JsonAssert.assertJson( getClass(), "rolePrincipals", principalsResultMapper );
    }

    private Principals createUserPrincipals()
    {
        final Principal user1 = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final Principal user2 = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user2" ) ).
            displayName( "User 2" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();

        return Principals.from( user1, user2 );
    }
}
