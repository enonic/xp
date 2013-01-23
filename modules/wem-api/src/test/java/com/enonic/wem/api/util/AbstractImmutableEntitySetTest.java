package com.enonic.wem.api.util;


import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import static junit.framework.Assert.assertEquals;

public class AbstractImmutableEntitySetTest
{
    @Test
    public void getSet_iterator_of_returned_set_iterates_over_items_in_inserted_order()
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
    public void isNotEmpty()
    {
        assertEquals( true, new MySet( ImmutableSet.of( "a", "b", "c" ) ).isNotEmpty() );
    }

    @Test
    public void isEmpty()
    {
        assertEquals( false, new MySet( ImmutableSet.of( "a", "b", "c" ) ).isEmpty() );
    }

    private class MySet
        extends AbstractImmutableEntitySet<String>
    {

        protected MySet( final ImmutableSet<String> set )
        {
            super( set );
        }
    }
}
