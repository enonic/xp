package com.enonic.xp.core.internal.concurrent;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtomicSortedListTest
{
    @Test
    void add_sorted()
    {
        final AtomicSortedList<Integer> sortedList = new AtomicSortedList<Integer>( Comparator.naturalOrder() );
        sortedList.add( 2 );
        sortedList.add( 2 );
        sortedList.add( 1 );
        sortedList.add( 3 );

        assertThat( sortedList.snapshot() ).containsExactly( 1, 2, 2, 3 );
    }

    @Test
    void remove_sorted()
    {
        final AtomicSortedList<Integer> sortedList = new AtomicSortedList<Integer>( Comparator.naturalOrder() );
        sortedList.add( 2 );
        sortedList.add( 1 );
        sortedList.add( 3 );
        sortedList.remove( 2 );

        assertThat( sortedList.snapshot() ).containsExactly( 1, 3 );
    }

    @Test
    void removeIf()
    {
        final AtomicSortedList<Integer> sortedList = new AtomicSortedList<Integer>( Comparator.naturalOrder() );
        sortedList.add( 2 );
        sortedList.add( 1 );
        sortedList.add( 3 );
        sortedList.add( 2 );

        sortedList.removeIf( value -> value == 2 );

        assertThat( sortedList.snapshot() ).containsExactly( 1, 3 );
    }

    @Test
    void removeIf_noMatch()
    {
        final AtomicSortedList<Integer> sortedList = new AtomicSortedList<Integer>( Comparator.naturalOrder() );
        sortedList.add( 1 );

        sortedList.removeIf( value -> value == 2 );

        assertThat( sortedList.snapshot() ).containsExactly( 1 );
    }

    @Test
    void snapshot()
    {
        final AtomicSortedList<Integer> sortedList = new AtomicSortedList<Integer>( Comparator.naturalOrder() );
        sortedList.add( 2 );
        sortedList.add( 1 );
        sortedList.add( 3 );

        final List<Integer> snapshot = sortedList.snapshot();

        sortedList.remove( 2 );

        assertThat( snapshot ).containsExactly( 1, 2, 3 );
    }
}
