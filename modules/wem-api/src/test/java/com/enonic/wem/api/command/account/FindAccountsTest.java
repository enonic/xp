package com.enonic.wem.api.command.account;

import org.junit.Test;

public class FindAccountsTest
{
    @Test
    public void testValid()
    {
        final FindAccounts command = new FindAccounts();

        command.validate();
    }
}
