package com.enonic.wem.api.account;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AccountKeySetTest
{
    @Test
    public void testFrom_string()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        assertFalse( set1.isEmpty() );
        assertEquals( 1, set1.getSize() );
        assertFalse( set1.contains( AccountKey.anonymous() ) );
        assertTrue( set1.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertEquals( "[user:other:dummy]", set1.toString() );

        final AccountKeySet set2 = AccountKeySet.from( "user:other:dummy", "user:other:dummy" );
        assertEquals( 1, set2.getSize() );
        assertEquals( "[user:other:dummy]", set2.toString() );

        final AccountKeySet set3 = AccountKeySet.from( "user:other:dummy", "user:other:you" );
        assertEquals( 2, set3.getSize() );
        assertEquals( "[user:other:dummy, user:other:you]", set3.toString() );
    }

    @Test
    public void testFrom_key()
    {
        final AccountKeySet set = AccountKeySet.from( AccountKey.from( "user:other:dummy" ) );
        assertFalse( set.isEmpty() );
        assertEquals( 1, set.getSize() );
        assertTrue( set.contains( AccountKey.from( "user:other:dummy" ) ) );
    }

    @Test
    public void testFrom_iterator()
    {
        final Collection<AccountKey> list1 = Lists.newArrayList();
        final AccountKeySet set1 = AccountKeySet.from( list1 );
        assertTrue( set1.isEmpty() );
        assertEquals( 0, set1.getSize() );

        final Collection<AccountKey> list2 = Lists.newArrayList();
        list2.add( AccountKey.from( "user:other:dummy" ) );

        final AccountKeySet set2 = AccountKeySet.from( list2 );
        assertFalse( set2.isEmpty() );
        assertEquals( 1, set2.getSize() );
    }

    @Test
    public void testIterator()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy", "user:other:you" );
        final Iterator<AccountKey> it = set.iterator();

        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertEquals( "user:other:dummy", it.next().toString() );
        assertTrue( it.hasNext() );
        assertEquals( "user:other:you", it.next().toString() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testGetSet()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy", "user:other:you" );
        final Set<AccountKey> other = set.getSet();

        assertNotNull( other );
        assertEquals( 2, other.size() );
        assertTrue( set.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertTrue( set.contains( AccountKey.from( "user:other:you" ) ) );
    }

    @Test
    public void testOnlyUsers()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party", "role:other:admin" );
        assertEquals( 3, set1.getSize() );

        final AccountKeySet set2 = set1.onlyUsers();
        assertEquals( 1, set2.getSize() );
        assertTrue( set2.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertFalse( set2.contains( AccountKey.from( "group:other:party" ) ) );
        assertFalse( set2.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testOnlyGroups()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party", "role:other:admin" );
        assertEquals( 3, set1.getSize() );

        final AccountKeySet set2 = set1.onlyGroups();
        assertEquals( 1, set2.getSize() );
        assertFalse( set2.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertTrue( set2.contains( AccountKey.from( "group:other:party" ) ) );
        assertFalse( set2.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testOnlyRoles()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party", "role:other:admin" );
        assertEquals( 3, set1.getSize() );

        final AccountKeySet set2 = set1.onlyRoles();
        assertEquals( 1, set2.getSize() );
        assertFalse( set2.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertFalse( set2.contains( AccountKey.from( "group:other:party" ) ) );
        assertTrue( set2.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testFilterTypes()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party", "role:other:admin" );
        assertEquals( 3, set1.getSize() );

        final AccountKeySet set2 = set1.filterTypes();
        assertEquals( 0, set2.getSize() );
        assertFalse( set2.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertFalse( set2.contains( AccountKey.from( "group:other:party" ) ) );
        assertFalse( set2.contains( AccountKey.from( "role:other:admin" ) ) );

        final AccountKeySet set3 = set1.filterTypes( AccountType.GROUP, AccountType.ROLE );
        assertEquals( 2, set3.getSize() );
        assertFalse( set3.contains( AccountKey.from( "user:other:dummy" ) ) );
        assertTrue( set3.contains( AccountKey.from( "group:other:party" ) ) );
        assertTrue( set3.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testEmpty()
    {
        final AccountKeySet set = AccountKeySet.empty();
        assertTrue( set.isEmpty() );
        assertEquals( 0, set.getSize() );
    }

    @Test
    public void testAdd()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party" );
        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );

        final AccountKeySet set3 = set1.add( set2 );
        assertEquals( 2, set1.getSize() );
        assertEquals( 1, set2.getSize() );
        assertEquals( 3, set3.getSize() );
        assertTrue( set3.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testRemove()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "group:other:party", "role:other:admin" );
        final AccountKeySet set2 = AccountKeySet.from( "user:other:dummy", "group:other:party" );

        final AccountKeySet set3 = set1.remove( set2 );
        assertEquals( 3, set1.getSize() );
        assertEquals( 2, set2.getSize() );
        assertEquals( 1, set3.getSize() );
        assertTrue( set3.contains( AccountKey.from( "role:other:admin" ) ) );
    }

    @Test
    public void testHashCode()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set2 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set3 = AccountKeySet.empty();

        assertTrue( set1.hashCode() == set2.hashCode() );
        assertFalse( set1.hashCode() == set3.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set2 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set3 = AccountKeySet.empty();

        assertTrue( set1.equals( set1 ) );
        assertTrue( set2.equals( set1 ) );
        assertFalse( set3.equals( set1 ) );
    }
}
