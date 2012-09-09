package com.enonic.wem.api.userstore;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class UserStoreNamesTest
{
    @Test
    public void testFrom_string()
    {
        final UserStoreNames set1 = UserStoreNames.from( "test" );
        assertFalse( set1.isEmpty() );
        assertEquals( 1, set1.getSize() );
        assertTrue( set1.contains( UserStoreName.from( "test" ) ) );
        assertEquals( "[test]", set1.toString() );

        final UserStoreNames set2 = UserStoreNames.from( "test", "test" );
        assertEquals( 1, set2.getSize() );
        assertEquals( "[test]", set2.toString() );

        final UserStoreNames set3 = UserStoreNames.from( "test", "other" );
        assertEquals( 2, set3.getSize() );
        assertEquals( "[test, other]", set3.toString() );
    }

    @Test
    public void testFrom_name()
    {
        final UserStoreNames set = UserStoreNames.from( UserStoreName.from( "test" ) );
        assertFalse( set.isEmpty() );
        assertEquals( 1, set.getSize() );
        assertTrue( set.contains( UserStoreName.from( "test" ) ) );
    }

    @Test
    public void testFrom_iterator()
    {
        final Collection<UserStoreName> list1 = Lists.newArrayList();
        final UserStoreNames set1 = UserStoreNames.from( list1 );
        assertTrue( set1.isEmpty() );
        assertEquals( 0, set1.getSize() );

        final Collection<UserStoreName> list2 = Lists.newArrayList();
        list2.add( UserStoreName.from( "test" ) );

        final UserStoreNames set2 = UserStoreNames.from( list2 );
        assertFalse( set2.isEmpty() );
        assertEquals( 1, set2.getSize() );
    }

    @Test
    public void testIterator()
    {
        final UserStoreNames set = UserStoreNames.from( "other", "test" );
        final Iterator<UserStoreName> it = set.iterator();

        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertEquals( "other", it.next().toString() );
        assertTrue( it.hasNext() );
        assertEquals( "test", it.next().toString() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testGetSet()
    {
        final UserStoreNames set = UserStoreNames.from( "other", "test" );
        final Set<UserStoreName> other = set.getSet();

        assertNotNull( other );
        assertEquals( 2, other.size() );
        assertTrue( set.contains( UserStoreName.from( "other" ) ) );
        assertTrue( set.contains( UserStoreName.from( "test" ) ) );
    }

    @Test
    public void testEmpty()
    {
        final UserStoreNames set = UserStoreNames.empty();
        assertTrue( set.isEmpty() );
        assertEquals( 0, set.getSize() );
    }

    @Test
    public void testAdd_keys()
    {
        final UserStoreNames original = UserStoreNames.from( "dummy", "other" );
        final UserStoreNames total = original.add( UserStoreName.from( "test" ) );
        testAdd( original, total );
    }

    @Test
    public void testAdd_iterator()
    {
        final UserStoreNames original = UserStoreNames.from( "dummy", "other" );
        final UserStoreNames total = original.add( Lists.newArrayList( UserStoreName.from( "test" ) ) );
        testAdd( original, total );
    }

    @Test
    public void testAdd_keyStrings()
    {
        final UserStoreNames original = UserStoreNames.from( "dummy", "other" );
        final UserStoreNames total = original.add( "test" );
        testAdd( original, total );
    }

    private void testAdd( final UserStoreNames original, final UserStoreNames total )
    {
        assertEquals( 2, original.getSize() );
        assertEquals( 3, total.getSize() );
        assertTrue( total.contains( UserStoreName.from( "test" ) ) );
    }

    @Test
    public void testRemove_keys()
    {
        final UserStoreNames original = UserStoreNames.from( "test", "other" );
        final UserStoreNames total = original.remove( UserStoreName.from( "other" ) );
        testRemove( original, total );
    }

    @Test
    public void testRemove_iterator()
    {
        final UserStoreNames original = UserStoreNames.from( "test", "other" );
        final UserStoreNames total = original.remove( Lists.newArrayList( UserStoreName.from( "other" ) ) );
        testRemove( original, total );
    }

    @Test
    public void testRemove_keyStrings()
    {
        final UserStoreNames original = UserStoreNames.from( "test", "other" );
        final UserStoreNames total = original.remove( "other" );
        testRemove( original, total );
    }

    private void testRemove( final UserStoreNames original, final UserStoreNames total )
    {
        assertEquals( 2, original.getSize() );
        assertEquals( 1, total.getSize() );
        assertTrue( total.contains( UserStoreName.from( "test" ) ) );
    }

    @Test
    public void testHashCode()
    {
        final UserStoreNames set1 = UserStoreNames.from( "test" );
        final UserStoreNames set2 = UserStoreNames.from( "test" );
        final UserStoreNames set3 = UserStoreNames.empty();

        assertTrue( set1.hashCode() == set2.hashCode() );
        assertFalse( set1.hashCode() == set3.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final UserStoreNames set1 = UserStoreNames.from( "test" );
        final UserStoreNames set2 = UserStoreNames.from( "test" );
        final UserStoreNames set3 = UserStoreNames.empty();

        assertTrue( set1.equals( set1 ) );
        assertTrue( set2.equals( set1 ) );
        assertFalse( set3.equals( set1 ) );
    }

    @Test
    public void testGetFirst()
    {
        final UserStoreNames set1 = UserStoreNames.empty();
        assertNull( set1.getFirst() );

        final UserStoreName name = UserStoreName.system();
        final UserStoreNames set2 = UserStoreNames.from( name );
        assertEquals( name, set2.getFirst() );
    }
}
