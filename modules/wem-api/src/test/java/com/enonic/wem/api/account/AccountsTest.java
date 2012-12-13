package com.enonic.wem.api.account;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public final class AccountsTest
{
    private UserAccount account1;

    private UserAccount account2;

    @Before
    public void setUp()
    {
        this.account1 = UserAccount.create( "other:dummy" );
        this.account2 = UserAccount.create( AccountKey.anonymous() );
    }

    @Test
    public void testFrom_accounts()
    {
        final Accounts list = Accounts.from( this.account1, this.account2 );
        assertFalse( list.isEmpty() );
        assertEquals( 2, list.getSize() );
    }

    @Test
    public void testFrom_collection()
    {
        final Collection<? extends Account> accounts = Lists.newArrayList( this.account1, this.account2 );

        final Accounts list = Accounts.from( accounts );
        assertFalse( list.isEmpty() );
        assertEquals( 2, list.getSize() );
    }

    @Test
    public void testFrom_iterator()
    {
        final Iterable<? extends Account> accounts = Lists.newArrayList( this.account1, this.account2 );

        final Accounts list = Accounts.from( accounts );
        assertFalse( list.isEmpty() );
        assertEquals( 2, list.getSize() );
    }

    @Test
    public void testGetList()
    {
        final Accounts list = Accounts.from( this.account1 );
        final List<Account> other = list.getList();

        assertNotNull( other );
        assertEquals( 1, other.size() );
    }

    @Test
    public void testEmpty()
    {
        final Accounts list = Accounts.empty();

        assertNotNull( list );
        assertEquals( 0, list.getSize() );
        assertTrue( list.isEmpty() );
    }

    @Test
    public void testIterator()
    {
        final Accounts list = Accounts.from( this.account1, this.account2 );
        final Iterator<Account> it = list.iterator();

        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( this.account1, it.next() );
        assertTrue( it.hasNext() );
        assertEquals( this.account2, it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testGetFirst()
    {
        final Accounts list1 = Accounts.from( this.account1, this.account2 );
        assertSame( this.account1, list1.first() );

        final Accounts list2 = Accounts.empty();
        assertNull( list2.first() );
    }

    @Test
    public void testHashCode()
    {
        final Accounts list1 = Accounts.from( this.account1 );
        final Accounts list2 = Accounts.from( this.account1 );
        final Accounts list3 = Accounts.empty();

        assertTrue( list1.hashCode() == list2.hashCode() );
        assertFalse( list1.hashCode() == list3.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final Accounts list1 = Accounts.from( this.account1 );
        final Accounts list2 = Accounts.from( this.account1 );
        final Accounts list3 = Accounts.empty();

        assertTrue( list1.equals( list1 ) );
        assertTrue( list2.equals( list1 ) );
        assertFalse( list3.equals( list1 ) );
    }

    @Test
    public void testGetKeys()
    {
        final Accounts list = Accounts.from( this.account1, this.account2 );
        final AccountKeys keys = list.getKeys();

        assertEquals( 2, keys.getSize() );
        assertTrue( keys.contains( this.account1.getKey() ) );
        assertTrue( keys.contains( this.account2.getKey() ) );
    }
}
