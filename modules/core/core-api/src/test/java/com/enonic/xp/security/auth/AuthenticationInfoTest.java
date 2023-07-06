package com.enonic.xp.security.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationInfoTest
{

    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static final Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void testWithoutPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
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
    public void testWithPrincipals()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
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
    public void testCopy()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
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
        throws Exception
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            description( "description" ).
            modifiedTime( Instant.now( clock ) ).
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
        throws Exception
    {
        final PropertySet data = new PropertySet();
        data.setString( "subString", "subStringValue" );
        data.setLong( "subLong", 123L );
        final PropertyTree userProfile = new PropertyTree();
        userProfile.setSet( "myApp", data );
        userProfile.setString( "string", "stringValue" );

        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
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
        throws Exception
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
        throws Exception
    {
        final AuthenticationInfo info = AuthenticationInfo.unAuthenticated();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );
    }

    @Test
    void testSerializationServiceAccount()
    {
        final PropertySet publicKey_1 = new PropertySet();
        publicKey_1.setString( "kid", "kid_1" );
        publicKey_1.setString( "publicKey", "publicKey_1" );

        final PropertySet publicKey_2 = new PropertySet();
        publicKey_2.setString( "kid", "kid_2" );
        publicKey_2.setString( "publicKey", "publicKey_2" );

        final PropertyTree idProviderData = new PropertyTree();
        idProviderData.addSet( "publicKeys", publicKey_1 );
        idProviderData.addSet( "publicKeys", publicKey_2 );

        final User user = User.create()
            .login( "service-account" )
            .key( PrincipalKey.ofUser( IdProviderKey.from( "system" ), "sa" ) )
            .serviceAccount( true )
            .idProviderData( idProviderData )
            .build();

        final AuthenticationInfo info = AuthenticationInfo.create().user( user ).build();

        final byte[] serializedObject = SerializableUtils.serialize( info );
        final AuthenticationInfo deserializedObject = (AuthenticationInfo) SerializableUtils.deserialize( serializedObject );

        assertEquals( deserializedObject, info );

        final PropertySet propertySet = removePublicKeys( idProviderData, List.of( "kid_1" ) );

        assertNotNull( propertySet );
    }

    private PropertySet removePublicKeys( final PropertyTree source, final List<String> keysToRemove )
    {
        final PropertySet result = new PropertySet();

        source.getSets( "publicKeys" ).forEach( propertySet -> {
            final String kid = propertySet.getString( "kid" );
            if ( !keysToRemove.contains( kid ) )
            {
                result.addSet( "publicKeys", propertySet );
            }
        } );

        return result;
    }
}
