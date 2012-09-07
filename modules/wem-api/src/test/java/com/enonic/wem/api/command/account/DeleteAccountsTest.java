package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKeys;

import static org.junit.Assert.*;

public class DeleteAccountsTest
{
    @Test
    public void testValid()
    {
        final DeleteAccounts command = new DeleteAccounts();

        assertNull( command.getKeys() );

        final AccountKeys keys = AccountKeys.from( "user:other:dummy" );
        command.keys( keys );
        assertSame( keys, command.getKeys() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKeys()
    {
        final DeleteAccounts command = new DeleteAccounts();
        command.keys( null );

        command.validate();
    }
}
