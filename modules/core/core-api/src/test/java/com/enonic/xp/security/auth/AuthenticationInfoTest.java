package com.enonic.xp.security.auth;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
