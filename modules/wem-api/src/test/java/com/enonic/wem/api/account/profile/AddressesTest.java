package com.enonic.wem.api.account.profile;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AddressesTest
{
    @Test
    public void testIterator()
    {
        final Address address = new Address();
        final Addresses list = Addresses.from( address );
        final Iterator<Address> it = list.iterator();

        assertNotNull( it );
        assertTrue( it.hasNext() );
        assertSame( address, it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testEmpty()
    {
        final Addresses set = Addresses.empty();
        assertTrue( set.isEmpty() );
        assertEquals( 0, set.getSize() );
    }

    @Test
    public void testGetList()
    {
        final Address address = new Address();
        final Addresses list = Addresses.from( address );
        final List<Address> other = list.getList();

        assertNotNull( other );
        assertEquals( 1, other.size() );
        assertSame( address, other.get( 0 ) );
    }

    @Test
    public void testFrom_addresses()
    {
        final Address address = new Address();
        final Addresses list = Addresses.from( address );
        assertFalse( list.isEmpty() );
        assertEquals( 1, list.getSize() );
    }

    @Test
    public void testFrom_iterator()
    {
        final Address address = new Address();
        final Addresses list = Addresses.from( Lists.newArrayList( address ) );
        assertFalse( list.isEmpty() );
        assertEquals( 1, list.getSize() );
    }

    @Test
    public void testAdd_addresses()
    {
        final Address address1 = new Address();
        final Address address2 = new Address();

        final Addresses list1 = Addresses.from( address1 );
        assertEquals( 1, list1.getSize() );

        final Addresses list2 = list1.add( address2 );
        assertEquals( 1, list1.getSize() );
        assertEquals( 2, list2.getSize() );
    }

    @Test
    public void testAdd_iterator()
    {
        final Address address1 = new Address();
        final Address address2 = new Address();

        final Addresses list1 = Addresses.from( address1 );
        assertEquals( 1, list1.getSize() );

        final Addresses list2 = list1.add( Lists.newArrayList( address2 ) );
        assertEquals( 1, list1.getSize() );
        assertEquals( 2, list2.getSize() );
    }

    @Test
    public void testGetPrimary()
    {
        final Address address1 = new Address();
        final Address address2 = new Address();

        final Addresses list1 = Addresses.empty();
        assertNull( list1.getPrimary() );

        final Addresses list2 = list1.add( address1 );
        assertSame( address1, list2.getPrimary() );

        final Addresses list3 = list2.add( address2 );
        assertSame( address1, list3.getPrimary() );
    }
}
