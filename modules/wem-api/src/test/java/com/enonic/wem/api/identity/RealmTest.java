package com.enonic.wem.api.identity;

import org.junit.Test;

import static org.junit.Assert.*;

public class RealmTest
{
    @Test
    public void testCreateRealm()
        throws Exception
    {
        final RealmKey realmKey = new RealmKey( "myrealm" );
        final Realm realm = Realm.newRealm().name( "my realm" ).key( realmKey ).build();

        assertEquals( "myrealm", realm.getKey().toString() );
        assertEquals( "my realm", realm.getName() );
    }

    @Test
    public void testCreateCopyRealm()
        throws Exception
    {
        final RealmKey realmKey = new RealmKey( "myrealm" );
        final Realm realm = Realm.newRealm().name( "my realm" ).key( realmKey ).build();
        final Realm realmCopy = Realm.newRealm( realm ).build();

        assertEquals( "myrealm", realmCopy.getKey().toString() );
        assertEquals( "my realm", realmCopy.getName() );
    }

    @Test
    public void testRealmKey()
        throws Exception
    {
        final RealmKey realmKey = new RealmKey( "myrealm" );
        final RealmKey realmKey2 = new RealmKey( "myrealm" );

        assertEquals( realmKey, realmKey2 );
        assertEquals( "myrealm", realmKey.toString() );
    }
}