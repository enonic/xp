package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.account.selector.AccountSelectors;

import static org.junit.Assert.*;

public class DeleteAccountsTest
{
    @Test
    public void testValid()
    {
        final DeleteAccounts command = new DeleteAccounts();

        assertNull( command.getSelector() );

        final AccountSelector selector = AccountSelectors.keys( "user:other:dummy" );
        command.selector( selector );
        assertSame( selector, command.getSelector() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullSelector()
    {
        final DeleteAccounts command = new DeleteAccounts();
        command.selector( null );

        command.validate();
    }
}

