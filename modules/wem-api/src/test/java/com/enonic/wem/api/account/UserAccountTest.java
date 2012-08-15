package com.enonic.wem.api.account;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UserAccountTest
    extends AccountTest<UserAccount>
{
    @Override
    protected UserAccount create( final String name )
    {
        return UserAccount.create( name );
    }

    @Test
    public void testBasic()
    {
        final UserAccount account = create( "other:dummy" );
        assertNotNull( account );
        assertNotNull( account.getKey() );
        assertEquals( "user:other:dummy", account.getKey().toString() );

        testBasic( account );

        assertNull( account.getEmail() );
        assertSame( account, account.email( "dummy@other.com" ) );
        assertEquals( "dummy@other.com", account.getEmail() );

        final byte[] photo = new byte[10];

        assertNull( account.getPhoto() );
        assertSame( account, account.photo( photo ) );
        assertSame( photo, account.getPhoto() );

        final DateTime now = DateTime.now();

        assertNull( account.getLastLoginTime() );
        assertSame( account, account.lastLoginTime( now ) );
        assertEquals( now, account.getLastLoginTime() );
    }

    @Test
    public void testCopy()
    {
        final byte[] photo = new byte[10];

        final UserAccount account = create( "other:dummy" );
        account.email( "dummy@other.com" );
        account.photo( photo );

        final UserAccount copy = testCopy( account );

        assertEquals( "dummy@other.com", copy.getEmail() );
        assertSame( photo, copy.getPhoto() );
    }
}
