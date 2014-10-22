package com.enonic.wem.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserStoreTest
{
    @Test
    public void testCreateRealm()
        throws Exception
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "myrealm" );
        final UserStore userStore = UserStore.newRealm().name( "my realm" ).key( userStoreKey ).build();

        assertEquals( "myrealm", userStore.getKey().toString() );
        assertEquals( "my realm", userStore.getName() );
    }

    @Test
    public void testCreateCopyRealm()
        throws Exception
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "myrealm" );
        final UserStore userStore = UserStore.newRealm().name( "my realm" ).key( userStoreKey ).build();
        final UserStore userStoreCopy = UserStore.newRealm( userStore ).build();

        assertEquals( "myrealm", userStoreCopy.getKey().toString() );
        assertEquals( "my realm", userStoreCopy.getName() );
    }

    @Test
    public void testRealmKey()
        throws Exception
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "myrealm" );
        final UserStoreKey userStoreKey2 = new UserStoreKey( "myrealm" );

        assertEquals( userStoreKey, userStoreKey2 );
        assertEquals( "myrealm", userStoreKey.toString() );
    }
}