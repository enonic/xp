package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKeys;

import static org.junit.Assert.*;

public class GetAccountsTest
{
    @Test
    public void testValid()
    {
        final AccountKeys keys = AccountKeys.empty();
        final GetAccounts command = new GetAccounts();

        command.keys( keys );
        assertSame( keys, command.getKeys() );

        assertFalse( command.isIncludeImage() );
        command.includeImage();
        assertTrue( command.isIncludeImage() );

        assertFalse( command.isIncludeMembers() );
        command.includeMembers();
        assertTrue( command.isIncludeMembers() );

        assertFalse( command.isIncludeProfile() );
        command.includeProfile();
        assertTrue( command.isIncludeProfile() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullSelector()
    {
        final GetAccounts command = new GetAccounts();
        command.keys( null );

        command.validate();
    }
}
