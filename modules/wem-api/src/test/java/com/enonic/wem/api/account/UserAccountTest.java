package com.enonic.wem.api.account;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.profile.UserProfile;

import static org.junit.Assert.*;

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
        return UserKey.from( qName );
    }

    @Override
    protected UserAccount create( final AccountKey key )
    {
        return UserAccount.create( key.asUser() );
    }

    @Override
    protected AccountKey createIllegalKey( final String qName )
    {
        return GroupKey.from( qName );
    }

    @Test
    public void testEmail()
    {
        final UserAccount account = create( "other:dummy" );
        assertNull( account.getEmail() );

        account.setEmail( "dummy@other.com" );
        assertEquals( "dummy@other.com", account.getEmail() );
    }

    @Test
    public void testImage()
    {
        final UserAccount account = create( "other:dummy" );
        assertNull( account.getImage() );

        final byte[] image = new byte[10];
        account.setImage( image );
        assertSame( image, account.getImage() );
    }

    @Test
    public void testModifiedTime()
    {
        final UserAccount account = create( "other:dummy" );
        assertNull( account.getLastLoginTime() );

        final DateTime now = DateTime.now();
        account.setLastLoginTime( now );
        assertEquals( now, account.getLastLoginTime() );
    }

    @Test
    public void testProfile()
    {
        final UserAccount account = create( "other:dummy" );
        assertNull( account.getProfile() );

        final UserProfile profile = new UserProfile();
        account.setProfile( profile );
        assertSame( profile, account.getProfile() );
    }
}
