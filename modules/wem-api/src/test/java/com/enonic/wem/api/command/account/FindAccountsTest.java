package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.query.AccountQuery;

public class FindAccountsTest
{
    @Test
    public void testValid()
    {
        final AccountQuery query = new AccountQuery();
        final FindAccounts command = new FindAccounts();

        assertNull( command.getQuery() );
        command.query( query );
        assertSame( query, command.getQuery() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullSelector()
    {
        final FindAccounts command = new FindAccounts();
        command.query( null );

        command.validate();
    }
}
