package com.enonic.wem.api.account.query;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.query.AccountFacets;
import com.enonic.wem.api.account.query.AccountResult;

import static org.junit.Assert.*;

public class AccountResultTest
{
    @Test
    public void testList()
    {
        final UserAccount account = UserAccount.create( "other:dummy" );

        final List<Account> list = Lists.newArrayList();
        list.add( account );

        final AccountResult result = new AccountResult( 10, list );
        assertEquals( 10, result.getTotalSize() );
        assertEquals( 1, result.getSize() );
        assertNotNull( result.getAll() );
        assertEquals( 1, result.getAll().size() );
        assertFalse( result.isEmpty() );
        assertSame( account, result.getAll().get( 0 ) );
        assertSame( account, result.first() );

        final Iterator<Account> it = result.iterator();
        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( account, it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testList_empty()
    {
        final List<Account> list = Lists.newArrayList();

        final AccountResult result = new AccountResult( 10, list );
        assertEquals( 10, result.getTotalSize() );
        assertEquals( 0, result.getSize() );
        assertNotNull( result.getAll() );
        assertEquals( 0, result.getAll().size() );
        assertTrue( result.isEmpty() );
        assertNull( result.first() );

        final Iterator<Account> it = result.iterator();
        assertNotNull( it );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testFacets()
    {
        final List<Account> list = Lists.newArrayList();

        final AccountResult result = new AccountResult( 10, list );
        assertNull( result.getFacets() );

        final AccountFacets facets = new AccountFacets();
        result.setFacets( facets );
        assertSame( facets, result.getFacets() );
    }
}
