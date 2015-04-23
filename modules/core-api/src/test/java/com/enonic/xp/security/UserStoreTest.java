package com.enonic.xp.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserStoreTest
{
    @Test
    public void testCreateUserStore()
        throws Exception
    {
        final UserStoreKey userStoreKey = UserStoreKey.from( "myUserStore" );
        final UserStore userStore = UserStore.newUserStore().displayName( "my user store" ).key( userStoreKey ).build();

        assertEquals( "myUserStore", userStore.getKey().toString() );
        assertEquals( "my user store", userStore.getDisplayName() );
    }

    @Test
    public void testCreateCopyUserStore()
        throws Exception
    {
        final UserStoreKey userStoreKey = UserStoreKey.from( "myUserStore" );
        final UserStore userStore = UserStore.newUserStore().displayName( "my user store" ).key( userStoreKey ).build();
        final UserStore userStoreCopy = UserStore.newUserStore( userStore ).build();

        assertEquals( "myUserStore", userStoreCopy.getKey().toString() );
        assertEquals( "my user store", userStoreCopy.getDisplayName() );
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
}