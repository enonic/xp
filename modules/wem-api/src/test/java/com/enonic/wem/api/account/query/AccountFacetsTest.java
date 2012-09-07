package com.enonic.wem.api.account.query;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacets;

import static org.junit.Assert.*;

public class AccountFacetsTest
{
    @Test
    public void testIterator()
    {
        final AccountFacets facets = new AccountFacets();
        assertNotNull( facets.getFacets() );
        assertTrue( facets.getFacets().isEmpty() );

        final AccountFacet facet = new AccountFacet( "name" );
        facets.addFacet( facet );

        assertNotNull( facets.getFacets() );
        assertEquals( 1, facets.getFacets().size() );
        assertSame( facet, facets.getFacets().get( 0 ) );

        final Iterator<AccountFacet> it = facets.iterator();
        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( facet, it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testIterator_empty()
    {
        final AccountFacets facets = new AccountFacets();
        assertNotNull( facets.getFacets() );
        assertTrue( facets.getFacets().isEmpty() );

        final Iterator<AccountFacet> it = facets.iterator();
        assertNotNull( it );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testGetFacet()
    {
        final AccountFacets facets = new AccountFacets();
        assertNull( facets.getFacet( "name" ) );

        final AccountFacet facet = new AccountFacet( "name" );
        facets.addFacet( facet );
        assertNotNull( facets.getFacet( "name" ) );
        assertSame( facet, facets.getFacet( "name" ) );
    }

    @Test
    public void testAsMap()
    {
        final AccountFacets facets = new AccountFacets();
        assertNotNull( facets.asMap() );
        assertEquals( 0, facets.asMap().size() );

        final AccountFacet facet = new AccountFacet( "name" );
        facets.addFacet( facet );
        assertNotNull( facets.asMap() );
        assertEquals( 1, facets.asMap().size() );
        assertSame( facet, facets.asMap().get( "name" ) );
    }
}
