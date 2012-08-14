package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;

import static org.junit.Assert.*;

public class FindMembershipsTest
{
    @Test
    public void testValid()
    {
        final AccountKey key = AccountKey.from( "user:other:dummy" );
        final FindMemberships command = new FindMemberships();

        command.key( key );
        assertEquals( key, command.getKey() );
        assertEquals( false, command.isIncludeTransitive() );

        command.includeTransitive();
        assertEquals( true, command.isIncludeTransitive() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKey()
    {
        final FindMemberships command = new FindMemberships();
        command.key( null );

        command.validate();
    }
}

