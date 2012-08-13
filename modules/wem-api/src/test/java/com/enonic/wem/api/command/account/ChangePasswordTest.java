package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.AccountKey;

public class ChangePasswordTest
{
    @Test
    public void testValid()
    {
        final AccountKey key = AccountKey.from( "user:other:dummy" );

        final ChangePassword command = new ChangePassword();
        command.key( key );
        command.password( "password" );

        assertEquals( key, command.getKey() );
        assertEquals( "password", command.getPassword() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKey()
    {
        final ChangePassword command = new ChangePassword();
        command.key( null );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullPassword()
    {
        final ChangePassword command = new ChangePassword();
        command.password( null );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_superUser()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.superUser() );
        command.password( "password" );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_anonymous()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.anonymous() );
        command.password( "password" );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_notUser()
    {
        final ChangePassword command = new ChangePassword();
        command.key( AccountKey.from( "group:other:dummy" ) );
        command.password( "password" );

        command.validate();
    }
}
