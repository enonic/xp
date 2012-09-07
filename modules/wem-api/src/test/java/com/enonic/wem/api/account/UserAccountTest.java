package com.enonic.wem.api.account;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.profile.UserProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UserAccountTest
    extends AccountTest<UserAccount>
{
    @Override
    protected UserAccount create( final String qName )
    {
        return UserAccount.create( qName );
    }

    @Override
    protected AccountKey createKey( final String qName )
    {
        return AccountKey.user( qName );
    }

    @Override
    protected UserAccount create( final AccountKey key )
    {
        return UserAccount.create( key );
    }

    @Override
    protected AccountKey createIllegalKey( final String qName )
    {
        return AccountKey.group( qName );
    }

    @Test
    public void testEmail()
    {
        final UserAccount account = create( "other:dummy" );
        assertFalse( account.isDirty() );
        assertNull( account.getEmail() );

        account.setEmail( "dummy@other.com" );
        assertEquals( "dummy@other.com", account.getEmail() );
        assertTrue( account.isDirty() );
    }

    @Test
    public void testImage()
    {
        final UserAccount account = create( "other:dummy" );
        assertFalse( account.isDirty() );
        assertNull( account.getImage() );

        final byte[] image = new byte[10];
        account.setImage( image );
        assertSame( image, account.getImage() );
        assertTrue( account.isDirty() );
    }

    @Test
    public void testModifiedTime()
    {
        final UserAccount account = create( "other:dummy" );
        assertFalse( account.isDirty() );
        assertNull( account.getLastLoginTime() );

        final DateTime now = DateTime.now();
        account.setLastLoginTime( now );
        assertEquals( now, account.getLastLoginTime() );
        assertFalse( account.isDirty() );
    }

    @Test
    public void testProfile()
    {
        final UserAccount account = create( "other:dummy" );
        assertFalse( account.isDirty() );
        assertNull( account.getProfile() );

        final UserProfile profile = new UserProfile();
        account.setProfile( profile );
        assertSame( profile, account.getProfile() );
        assertTrue( account.isDirty() );
    }
}
