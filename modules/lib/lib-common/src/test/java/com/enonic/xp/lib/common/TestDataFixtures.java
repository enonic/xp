package com.enonic.xp.lib.common;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;

public class TestDataFixtures
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static final Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    static User getTestUser()
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
            key( PrincipalKey.ofGroup( IdProviderKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            description( "description" ).
            build();
    }
}
