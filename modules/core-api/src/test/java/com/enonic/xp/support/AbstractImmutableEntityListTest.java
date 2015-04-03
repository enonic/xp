package com.enonic.xp.support;


import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class AbstractImmutableEntityListTest
{

    @Test
    public void isNotEmpty()
    {
        assertEquals( true, new MyList( ImmutableList.of( "a", "b", "c" ) ).isNotEmpty() );
    }

    @Test
    public void isEmpty()
    {
        assertEquals( false, new MyList( ImmutableList.of( "a", "b", "c" ) ).isEmpty() );
    }

    @Test
    public void testAccessors()
    {
        MyList list = new MyList( ImmutableList.of( "a", "b", "c" ) );
        MyList emptyList = new MyList( ImmutableList.of() );

        assertEquals( "a", list.get( 0 ) );
        assertEquals( "b", list.get( 1 ) );
        assertEquals( "c", list.get( 2 ) );

        assertEquals( 3, list.getSize() );

        assertTrue( list.contains( "b" ) );

        assertEquals( "a", list.first() );
        assertNull( emptyList.first() );
        assertEquals( "c", list.last() );
        assertNull( emptyList.last() );
    }

    @Test
    public void testEquals()
    {
        MyList list1 = new MyList( ImmutableList.of( "a", "b", "c" ) );
        MyList list2 = new MyList( ImmutableList.of( "a", "c", "b" ) );

        assertFalse( list1.equals( list2 ) );
    }

    private class MyList
        extends AbstractImmutableEntityList<String>
    {

        protected MyList( final ImmutableList<String> set )
        {
            super( set );
        }
    }
}
