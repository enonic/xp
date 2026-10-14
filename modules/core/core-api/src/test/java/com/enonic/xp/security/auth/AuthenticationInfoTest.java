package com.enonic.xp.security.auth;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationInfoTest
{
    @Test
    void testWithoutPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final AuthenticationInfo info = AuthenticationInfo.create().user( user ).build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "user:myidprovider:userid" ), info.getUser().getKey() );
        assertEquals( 1, info.getPrincipals().getSize() );
        assertEquals( user.getKey(), info.getPrincipals().first() );
        assertFalse( info.hasRole( "userid" ) );
    }

    @Test
    void testWithPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final IdProviderKey idProvider = IdProviderKey.from( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( idProvider, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "group:myStore:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principals( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        assertEquals( "userlogin", info.getUser().getLogin() );
        assertEquals( "my user", info.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "user:myidprovider:userid" ), info.getUser().getKey() );
        assertEquals( 4, info.getPrincipals().getSize() );
        assertTrue( info.getPrincipals().contains( PrincipalKey.from( "user:myidprovider:userid" ) ) );
        assertTrue( info.getPrincipals().contains( group1 ) );
        assertTrue( info.getPrincipals().contains( group2 ) );
        assertTrue( info.getPrincipals().contains( role1 ) );
        assertFalse( info.hasRole( "userid" ) );
        assertFalse( info.hasRole( "group1" ) );
        assertFalse( info.hasRole( "group2" ) );
        assertTrue( info.hasRole( "administrators" ) );
    }

    @Test
    void testActor()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();
        final PrincipalKey service = PrincipalKey.from( "user:myidprovider:automation" );

        final AuthenticationInfo plain = AuthenticationInfo.create().user( user ).build();
        assertEquals( null, plain.getActor(), "a user acts for themselves unless said otherwise" );

        final AuthenticationInfo acted = AuthenticationInfo.create().user( user ).actor( service ).build();
        assertEquals( service, acted.getActor() );
        assertFalse( acted.getPrincipals().contains( service ), "the actor is not a principal of the authentication" );
        assertEquals( acted, AuthenticationInfo.copyOf( acted ).build(), "a copy keeps the actor" );
        assertFalse( acted.equals( plain ) );

        final AuthenticationInfo roundTripped = (AuthenticationInfo) SerializableUtils.deserialize( SerializableUtils.serialize( acted ) );
        assertEquals( acted, roundTripped );
        assertEquals( service, roundTripped.getActor() );

        assertEquals( null, AuthenticationInfo.unAuthenticated().getActor() );
    }

    @Test
    void testRestriction()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "alice" ) ).login( "alice" ).build();
        final AuthenticationInfo plain = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        assertEquals( RoleRestriction.none(), plain.getRestriction() );
        assertEquals( RoleRestriction.none(), AuthenticationInfo.unAuthenticated().getRestriction() );

        final RoleRestriction limit = RoleRestriction.to( RoleKeys.ADMIN_LOGIN, RoleKeys.ADMIN );
        final AuthenticationInfo limited =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).restriction( limit ).build();
        assertEquals( limit, limited.getRestriction() );
        assertEquals( limit, AuthenticationInfo.copyOf( limited ).build().getRestriction() );
        assertNotEquals( plain, limited );

        for ( AuthenticationInfo info : new AuthenticationInfo[]{plain, limited,
            AuthenticationInfo.create().user( user ).restriction( RoleRestriction.to() ).build()} )
        {
            final AuthenticationInfo roundTripped = (AuthenticationInfo) SerializableUtils.deserialize( SerializableUtils.serialize( info ) );
            assertEquals( info.getRestriction(), roundTripped.getRestriction() );
            assertEquals( info, roundTripped );
        }
    }

    @Test
    void testCopy()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final IdProviderKey idProvider = IdProviderKey.from( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( idProvider, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "group:myStore:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principals( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        final AuthenticationInfo copy = AuthenticationInfo.copyOf( info ).build();
        assertEquals( info, copy );
        assertEquals( info.hashCode(), copy.hashCode() );
    }

    @Test
    void testSerialization()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            description( "description" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final IdProviderKey idProvider = IdProviderKey.from( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( idProvider, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "group:myStore:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principals( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );
    }

    @Test
    void testSerializationWithUserProfile()
    {
        final PropertyTree userProfile = new PropertyTree();
        final PropertySet data = userProfile.newSet();
        data.setString( "subString", "subStringValue" );
        data.setLong( "subLong", 123L );
        userProfile.setSet( "myApp", data );
        userProfile.setString( "string", "stringValue" );

        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            profile( userProfile ).
            build();

        final IdProviderKey idProvider = IdProviderKey.from( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( idProvider, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "group:myStore:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "role:administrators" );
        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            principals( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );
    }

    @Test
    void testSerializationMinimalFields()
    {
        final User user = User.create().
            login( "userlogin" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            build();

        final AuthenticationInfo info = AuthenticationInfo.create().
            user( user ).
            build();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );
    }

    @Test
    void testSerializationUnauthenticated()
    {
        final AuthenticationInfo info = AuthenticationInfo.unAuthenticated();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );
    }
}
