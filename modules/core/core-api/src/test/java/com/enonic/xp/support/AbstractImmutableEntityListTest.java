package com.enonic.xp.support;


import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractImmutableEntityListTest
{

    @Test
    void isNotEmpty()
    {
        assertTrue( new MyList( List.of( "a", "b", "c" ) ).isNotEmpty() );
    }

    @Test
    void isEmpty()
    {
        assertFalse( new MyList( List.of( "a", "b", "c" ) ).isEmpty() );
    }

    @Test
    void testAccessors()
    {
        MyList list = new MyList( List.of( "a", "b", "c" ) );
        MyList emptyList = new MyList( List.of() );

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
    void testEquals()
    {
        MyList list1 = new MyList( List.of( "a", "b", "c" ) );
        MyList list2 = new MyList( List.of( "a", "c", "b" ) );

        assertNotEquals( list1, list2 );
    }

    private static class MyList
        extends AbstractImmutableEntityList<String>
    {

        protected MyList( final List<String> set )
        {
            super( ImmutableList.copyOf( set ) );
        }
    }
}
