package com.enonic.wem.api.account.selector;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKeys;

import static org.junit.Assert.*;

public class AccountKeySelectorTest
{
    @Test
    public void testSimple()
    {
        final AccountKeys set = AccountKeys.from( "user:other:dummy" );
        final AccountKeySelector selector = new AccountKeySelector( set );
        assertSame( set, selector.getKeys() );
    }
}
