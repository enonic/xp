package com.enonic.xp.lib.common;

import java.time.Instant;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;

public class TestDataFixtures
{
    static User getTestUser()
    {
        return User.create().
            key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
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
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            description( "description" ).
            build();
    }

    static Group getTestGroup()
    {
        return Group.create().
            key( PrincipalKey.ofGroup( IdProviderKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            description( "description" ).
            build();
    }
}
