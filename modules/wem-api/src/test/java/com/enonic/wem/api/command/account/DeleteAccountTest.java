package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;

import static org.junit.Assert.*;

public class DeleteAccountTest
{
    @Test
    public void testValid()
    {
        final DeleteAccount command = new DeleteAccount();

        assertNull( command.getKey() );

        final AccountKey key = AccountKey.from( "user:other:dummy" );
        command.key( key );
        assertSame( key, command.getKey() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKey()
    {
        final DeleteAccount command = new DeleteAccount();
        command.key( null );

        command.validate();
    }
}
