package com.enonic.xp.security;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;

import static org.junit.Assert.*;

public class UserStoreTest
{
    @Test
    public void testCreateUserStore()
        throws Exception
    {
        final UserStoreKey userStoreKey = UserStoreKey.from( "myUserStore" );
        final UserStore userStore = UserStore.create().displayName( "my user store" ).key( userStoreKey ).build();

        assertEquals( "myUserStore", userStore.getKey().toString() );
        assertEquals( "my user store", userStore.getDisplayName() );
    }

    @Test
    public void testCreateUserStoreFromSource()
        throws Exception
    {
        final UserStoreKey key = UserStoreKey.from( "myUserStore" );
        final String displayName = "My user store";
        final String description = "Description";
        final AuthConfig authConfig = AuthConfig.create().applicationKey( ApplicationKey.SYSTEM ).config( new PropertyTree() ).build();

        final UserStore source =
            UserStore.create().key( key ).displayName( displayName ).description( description ).authConfig( authConfig ).build();
        final UserStore userStore = UserStore.create( source ).build();

        assertTrue( userStore.getKey().equals( key ) );
        assertEquals( displayName, userStore.getDisplayName() );
        assertEquals( description, userStore.getDescription() );
        assertTrue( userStore.getAuthConfig().equals( authConfig ) );
    }

    @Test
    public void testUserStoreKey()
        throws Exception
    {
        final UserStoreKey userStoreKey = UserStoreKey.from( "myUserStore" );
        final UserStoreKey userStoreKey2 = UserStoreKey.from( "myUserStore" );

        assertEquals( userStoreKey, userStoreKey2 );
        assertEquals( "myUserStore", userStoreKey.toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserStoreKeyCharacter1()
        throws Exception
    {
        UserStoreKey.from( "my<UserStore" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserStoreKeyCharacter2()
        throws Exception
    {
        UserStoreKey.from( "myUser>Store" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserStoreKeyCharacter3()
        throws Exception
    {
        UserStoreKey.from( "myUser\"Store" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserStoreKeyCharacter4()
        throws Exception
    {
        UserStoreKey.from( "myUserSt'ore" );
    }

}
