package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class IndexSourceOrderbyValueResolverTest
{

    @Test
    public void testNull()
    {
        assertNull( IndexSourceOrderbyValueResolver.getOrderbyValue( null ) );
    }

    @Test
    public void testOrderbyValuesForInt()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = IndexSourceOrderbyValueResolver.getOrderbyValue( 1 );
        String two = IndexSourceOrderbyValueResolver.getOrderbyValue( 2 );
        String ten = IndexSourceOrderbyValueResolver.getOrderbyValue( 10 );
        String hundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 100 );
        String twohundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 200 );

        orderedList.add( twohundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( twohundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForNumberAsString()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = IndexSourceOrderbyValueResolver.getOrderbyValue( "1" );
        String two = IndexSourceOrderbyValueResolver.getOrderbyValue( "2" );
        String ten = IndexSourceOrderbyValueResolver.getOrderbyValue( "10" );
        String hundred = IndexSourceOrderbyValueResolver.getOrderbyValue( "100" );
        String twohundred = IndexSourceOrderbyValueResolver.getOrderbyValue( "200" );

        orderedList.add( twohundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( twohundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForLong()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = IndexSourceOrderbyValueResolver.getOrderbyValue( 1L );
        String two = IndexSourceOrderbyValueResolver.getOrderbyValue( 2L );
        String ten = IndexSourceOrderbyValueResolver.getOrderbyValue( 10L );
        String hundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 100L );
        String twohundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 200L );

        orderedList.add( twohundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( twohundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForDouble()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = IndexSourceOrderbyValueResolver.getOrderbyValue( 1.0 );
        String two = IndexSourceOrderbyValueResolver.getOrderbyValue( 2.0 );
        String ten = IndexSourceOrderbyValueResolver.getOrderbyValue( 10.0 );
        String hundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 100.0 );
        String twohundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 200.0 );

        orderedList.add( twohundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( twohundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForFloat()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = IndexSourceOrderbyValueResolver.getOrderbyValue( 1f );
        String two = IndexSourceOrderbyValueResolver.getOrderbyValue( 2f );
        String ten = IndexSourceOrderbyValueResolver.getOrderbyValue( 10f );
        String hundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 100f );
        String twohundred = IndexSourceOrderbyValueResolver.getOrderbyValue( 200f );

        orderedList.add( twohundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( twohundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForDate()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();
        IndexSourceOrderbyValueResolver.getOrderbyValue( 1 );

        String first = IndexSourceOrderbyValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 1 ).toDate() );
        String second = IndexSourceOrderbyValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 2 ).toDate() );
        String third = IndexSourceOrderbyValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 10 ).toDate() );
        String fourth = IndexSourceOrderbyValueResolver.getOrderbyValue( new DateTime( 2014, 1, 1, 1, 2 ).toDate() );
        String fifth = IndexSourceOrderbyValueResolver.getOrderbyValue( new DateTime( 2014, 1, 1, 1, 10 ).toDate() );

        orderedList.add( fifth );
        orderedList.add( first );
        orderedList.add( fourth );
        orderedList.add( second );
        orderedList.add( third );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( first, iterator.next() );
        assertEquals( second, iterator.next() );
        assertEquals( third, iterator.next() );
        assertEquals( fourth, iterator.next() );
        assertEquals( fifth, iterator.next() );
    }


}
