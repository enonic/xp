package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.AccountKey;

public class ChangePasswordTest
{
    @Test
    public void testValid()
    {
        final ChangePassword command = new ChangePassword();

        final AccountKey key = AccountKey.from( "user:other:dummy" );
        command.key( key );
        assertEquals( key, command.getKey() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_null()
    {
        final ChangePassword command = new ChangePassword();
        command.key( null );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_superUser()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.superUser() );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_anonymous()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.anonymous() );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_notUser()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.from( "group:other:dummy" ) );

        command.validate();
    }
}
