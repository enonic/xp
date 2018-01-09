package com.enonic.xp.lib.common;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;

public class TestDataFixtures
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    static UserStore getTestUserStore()
    {
        return UserStore.create().
            key( UserStoreKey.from( "userStoreTestKey" ) ).
            description( "User store used for testing" ).
            displayName( "User store test" ).
            authConfig( getTestAuthConfig() ).
            build();
    }

    static AuthConfig getTestAuthConfig()
    {
        return AuthConfig.create().
            applicationKey( ApplicationKey.from( "com.enonic.app.test" ) ).
            config( getConfig() ).
            build();
    }

    private static PropertyTree getConfig()
    {
        final PropertySet passwordPropertySet = new PropertySet();
        passwordPropertySet.setString( "email", "noreply@example.com" );
        passwordPropertySet.setString( "site", "MyWebsite" );

        final PropertyTree config = new PropertyTree();
        config.setString( "title", "App Title" );
        config.setBoolean( "avatar", true );
        config.setSet( "forgotPassword", passwordPropertySet );

        return config;
    }

    static AuthDescriptor getTestAuthDescriptor()
    {
        return AuthDescriptor.create().
            key( ApplicationKey.from( "com.enonic.app.test" ) ).
            mode( AuthDescriptorMode.LOCAL ).
            config( Form.create().build() ).
            build();
    }

    static User getTestUser()
    {
        return User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            profile( getProfile() ).
            build();
    }

    private static PropertyTree getProfile()
    {
        final PropertySet appPropertySet = new PropertySet();
        appPropertySet.setString( "subString", "subStringValue" );
        appPropertySet.setLong( "subLong", 123l );

        final PropertyTree profile = new PropertyTree();
        profile.setSet( "myApp", appPropertySet );
        profile.setString( "string", "stringValue" );

        return profile;
    }

    static Role getTestRole()
    {
        return Role.create().
            key( PrincipalKey.ofRole( "aRole" ) ).
            displayName( "Role Display Name" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description" ).
            build();
    }

    static Group getTestGroup()
    {
        return Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description" ).
            build();
    }
}
