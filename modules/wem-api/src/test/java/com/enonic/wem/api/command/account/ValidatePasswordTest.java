package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;

import static org.junit.Assert.*;

public class ValidatePasswordTest
{
    @Test
    public void testValid()
    {
        final AccountKey key = AccountKey.from( "user:other:dummy" );

        final ValidatePassword command = new ValidatePassword();
        command.key( key );
        command.password( "password" );

        assertEquals( key, command.getKey() );
        assertEquals( "password", command.getPassword() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKey()
    {
        final ValidatePassword command = new ValidatePassword();
        command.key( null );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullPassword()
    {
        final ValidatePassword command = new ValidatePassword();
        command.password( null );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_anonymous()
    {
        final ValidatePassword command = new ValidatePassword();
        command.key( AccountKey.anonymous() );
        command.password( "password" );

        command.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValid_notUser()
    {
        final ValidatePassword command = new ValidatePassword();
        command.key( AccountKey.from( "group:other:dummy" ) );
        command.password( "password" );

        command.validate();
    }
}
