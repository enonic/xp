package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

public class TestDataFixtures
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static final Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    public static User getTestUser()
    {
        return User.create().
            key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            profile( getProfile() ).
            build();
    }

    private static PropertyTree getProfile()
    {
        final PropertyTree profile = new PropertyTree();
        final PropertySet appPropertySet = profile.newSet();
        appPropertySet.setString( "subString", "subStringValue" );
        appPropertySet.setLong( "subLong", 123L );

        profile.setSet( "myApp", appPropertySet );
        profile.setString( "string", "stringValue" );

        return profile;
    }

    public static User getTestUser2()
    {

        return User.create().
            key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user2" ) ).
            displayName( "User 2" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
    }

    public static User getTestUserWithProfile()
    {
        final PropertyTree profile = new PropertyTree();

        final PropertySet data = profile.newSet();
        data.setString( "untouchedString", "originalValue" );
        data.setBoolean( "untouchedBoolean", true );
        data.setDouble( "untouchedDouble", 2.0 );
        data.setLong( "untouchedLong", 2L );
        data.setLink( "untouchedLink", Link.from( "myLink" ) );
        data.setInstant( "untouchedInstant", Instant.parse( "2017-01-02T10:00:00Z" ) );
        data.setBinaryReference( "untouchedBinaryRef", BinaryReference.from( "abcd" ) );
        data.setGeoPoint( "untouchedGeoPoint", GeoPoint.from( "30,-30" ) );
        data.setLocalDate( "untouchedLocalDate", LocalDate.parse( "2017-03-24" ) );
        data.setReference( "untouchedReference", Reference.from( "myReference" ) );

        profile.setSet( "myApp", data );

        return User.create().
            key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            profile( profile ).
            build();
    }

    public static User getTestUserWithoutEmail()
    {
        return User.create().
            key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            login( "user1" ).
            build();
    }

    public static Role getTestRole()
    {
        return Role.create().
            key( PrincipalKey.ofRole( "aRole" ) ).
            displayName( "Role Display Name" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description" ).
            build();
    }

    public static Group getTestGroup()
    {
        return Group.create().
            key( PrincipalKey.ofGroup( IdProviderKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description" ).
            build();
    }

    public static IdProvider getTestIdProvider()
    {
        return IdProvider.create().
            key( IdProviderKey.from( "idProviderTestKey" ) ).
            description( "Id Provider used for testing" ).
            displayName( "Id Provider test" ).
            idProviderConfig( getTestIdProviderConfig() ).
            build();
    }

    private static IdProviderConfig getTestIdProviderConfig()
    {
        final PropertyTree config = new PropertyTree();
        final PropertySet backgroundPropertySet = config.newSet();
        backgroundPropertySet.setString( "subString", "subStringValue" );
        backgroundPropertySet.setLong( "subLong", 123L );

        config.setSet( "set", backgroundPropertySet );
        config.setString( "string", "stringValue" );

        return IdProviderConfig.create().
            applicationKey( ApplicationKey.from( "com.enonic.app.test" ) ).
            config( config ).
            build();
    }

    public static AuthenticationInfo createAuthenticationInfo()
    {
        return AuthenticationInfo.create().user( getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();
    }
}
