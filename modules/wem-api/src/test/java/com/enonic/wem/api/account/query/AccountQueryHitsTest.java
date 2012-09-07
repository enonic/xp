package com.enonic.wem.api.account.query;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.result.AccountFacets;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AccountQueryHitsTest
{
    @Test
    public void testHits()
    {
        final AccountKeys keys = AccountKeys.from( "user:other:dummy" );

        final AccountQueryHits result = new AccountQueryHits( 10, keys );
        assertEquals( 10, result.getTotalSize() );
        assertEquals( 1, result.getSize() );
        assertFalse( result.isEmpty() );
        assertSame( keys, result.getKeys() );

        final Iterator<AccountKey> it = result.iterator();
        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( keys.getFirst(), it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testHits_empty()
    {
        final AccountKeys keys = AccountKeys.empty();

        final AccountQueryHits result = new AccountQueryHits( 10, keys );
        assertEquals( 10, result.getTotalSize() );
        assertEquals( 0, result.getSize() );
        assertTrue( result.isEmpty() );
        assertSame( keys, result.getKeys() );

        final Iterator<AccountKey> it = result.iterator();
        assertNotNull( it );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testFacets()
    {
        final AccountQueryHits result = new AccountQueryHits( 10, AccountKeys.empty() );
        assertNull( result.getFacets() );

        final AccountFacets facets = new AccountFacets();
        result.setFacets( facets );
        assertSame( facets, result.getFacets() );
    }
}
