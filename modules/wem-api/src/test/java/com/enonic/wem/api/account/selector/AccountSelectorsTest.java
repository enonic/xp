package com.enonic.wem.api.account.selector;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;

import static org.junit.Assert.*;

public class AccountSelectorsTest
{
    @Test
    public void testKeys1()
    {
        final AccountKeySelector selector = AccountSelectors.keys( "user:other:dummy" );
        assertNotNull( selector );
        assertNotNull( selector.getKeys() );
        assertEquals( 1, selector.getKeys().getSize() );
    }

    @Test
    public void testKeys2()
    {
        final AccountKeySelector selector = AccountSelectors.keys( AccountKey.from( "user:other:dummy" ) );
        assertNotNull( selector );
        assertNotNull( selector.getKeys() );
        assertEquals( 1, selector.getKeys().getSize() );
    }

    @Test
    public void testKeys3()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySelector selector = AccountSelectors.keys( set );
        assertNotNull( selector );
        assertNotNull( selector.getKeys() );
        assertSame( set, selector.getKeys() );
        assertEquals( 1, selector.getKeys().getSize() );
    }

    @Test
    public void testKeys4()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySelector selector = AccountSelectors.keys( set.getSet() );
        assertNotNull( selector );
        assertNotNull( selector.getKeys() );
        assertEquals( 1, selector.getKeys().getSize() );
    }

    @Test
    public void testQuery()
    {
        final AccountQuery selector = AccountSelectors.query( "text" );
        assertNotNull( selector );
        assertEquals( "text", selector.getQuery() );
    }
}
