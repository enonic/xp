package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class TestDataFixtures
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    public static User getTestUser()
    {
        return User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
    }

    public static User getTestUser2()
    {
        return User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user2" ) ).
            displayName( "User 2" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user2@enonic.com" ).
            login( "user2" ).
            build();
    }

    public static User getTestUserWithoutEmail()
    {
        return User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
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
            build();
    }

    public static Group getTestGroup()
    {
        return Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            build();
    }

    public static UserStore getTestUserStore()
    {
        return UserStore.create().
            key( UserStoreKey.from( "userStoreTestKey" ) ).
            description( "User store used for testing" ).
            displayName( "User store test" ).
            authConfig( getTestAuthConfig() ).
            build();
    }

    private static AuthConfig getTestAuthConfig()
    {
        final PropertySet backgroundPropertySet = new PropertySet();
        backgroundPropertySet.setString( "subString", "subStringValue" );
        backgroundPropertySet.setLong( "subLong", 123l );

        final PropertyTree config = new PropertyTree();
        config.setSet( "set", backgroundPropertySet );
        config.setString( "string", "stringValue" );

        return AuthConfig.create().
            applicationKey( ApplicationKey.from( "com.enonic.app.test" ) ).
            config( config ).
            build();
    }


    public static AuthenticationInfo createAuthenticationInfo()
    {
        return AuthenticationInfo.create().user( getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();
    }
}
