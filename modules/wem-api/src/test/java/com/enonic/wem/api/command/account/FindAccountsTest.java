package com.enonic.wem.api.command.account;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.selector.AccountSelector;

public class FindAccountsTest
{
    @Test
    public void testValid()
    {
        final AccountSelector selector = Mockito.mock( AccountSelector.class );
        final FindAccounts command = new FindAccounts();

        command.selector( selector );
        assertSame( selector, command.getSelector() );

        assertFalse( command.isIncludePhoto() );
        command.includePhoto();
        assertTrue( command.isIncludePhoto() );

        assertFalse( command.isIncludeMembers() );
        command.includeMembers();
        assertTrue( command.isIncludeMembers() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullSelector()
    {
        final FindAccounts command = new FindAccounts();
        command.selector( null );

        command.validate();
    }
}
