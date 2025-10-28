package com.enonic.xp.support;


import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractImmutableEntitySetTest
{
    @Test
    void getSet_iterator_of_returned_set_iterates_over_items_in_inserted_order()
    {
        // setup
        MySet mySet = new MySet( ImmutableSet.of( "a", "b", "c" ) );

        // exercise & verify
        final Iterator<String> iterator = mySet.getSet().iterator();

        assertEquals( "a", iterator.next() );
        assertEquals( "b", iterator.next() );
        assertEquals( "c", iterator.next() );
    }

    @Test
    void isNotEmpty()
    {
        assertEquals( true, new MySet( ImmutableSet.of( "a", "b", "c" ) ).isNotEmpty() );
    }

    @Test
    void isEmpty()
    {
        assertEquals( false, new MySet( ImmutableSet.of( "a", "b", "c" ) ).isEmpty() );
    }

    private static class MySet
        extends AbstractImmutableEntitySet<String>
    {

        protected MySet( final ImmutableSet<String> set )
        {
            super( set );
        }
    }
}
