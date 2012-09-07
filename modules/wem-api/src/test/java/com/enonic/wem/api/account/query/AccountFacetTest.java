package com.enonic.wem.api.account.query;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;

import static org.junit.Assert.*;

public class AccountFacetTest
{
    @Test
    public void testName()
    {
        final AccountFacet facet = new AccountFacet( "name" );
        assertEquals( "name", facet.getName() );
    }

    @Test
    public void testIterator()
    {
        final AccountFacet facet = new AccountFacet( "name" );
        assertNotNull( facet.getEntries() );
        assertTrue( facet.getEntries().isEmpty() );

        final AccountFacetEntry entry = new AccountFacetEntry( "term", 10 );
        facet.addEntry( entry );

        assertNotNull( facet.getEntries() );
        assertEquals( 1, facet.getEntries().size() );
        assertSame( entry, facet.getEntries().get(0) );

        final Iterator<AccountFacetEntry> it = facet.iterator();
        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( entry, it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testIterator_empty()
    {
        final AccountFacet facet = new AccountFacet( "name" );
        assertNotNull( facet.getEntries() );
        assertTrue( facet.getEntries().isEmpty() );

        final Iterator<AccountFacetEntry> it = facet.iterator();
        assertNotNull( it );
        assertFalse( it.hasNext() );
    }
}
