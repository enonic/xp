package com.enonic.wem.api.account.query;

import org.junit.Test;

import com.enonic.wem.api.account.query.AccountFacetEntry;

import static org.junit.Assert.*;

public class AccountFacetEntryTest
{
    @Test
    public void testBasic()
    {
        final AccountFacetEntry entry = new AccountFacetEntry( "term", 20 );
        assertEquals( "term", entry.getTerm() );
        assertEquals( 20, entry.getCount() );
    }
}
