package com.enonic.wem.api.account.selector;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKeySet;

import static org.junit.Assert.*;

public class AccountKeySelectorImplTest
{
    @Test
    public void testSimple()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySelectorImpl selector = new AccountKeySelectorImpl( set );
        assertSame( set, selector.getKeys() );
    }
}
