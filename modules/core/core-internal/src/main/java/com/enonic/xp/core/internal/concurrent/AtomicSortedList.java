package com.enonic.xp.core.internal.concurrent;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aa atomic sorted list of elements.
 * It does not implement {@link List}, as it serves different purpose: Keep OSGi MULTIPLE DYNAMIC References in a specified order.
 *
 * @param <T> the type of elements in this list
 */
public class AtomicSortedList<T>
{
    private final Comparator<T> comparator;

    private final AtomicReference<List<T>> elementsRef = new AtomicReference<>( List.of() );

    /**
     * Constructs a new empty AtomicSortedList, sorted according to the specified comparator.
     *
     * @param comparator the comparator that will be used to sort this AtomicSortedList
     */
    public AtomicSortedList( final Comparator<T> comparator )
    {
        this.comparator = Objects.requireNonNull( comparator );
    }

    /**
     * Ands an element to this list, preserving sort order.
     *
     * @param element an element to add to this list
     */
    public void add( T element )
    {
        elementsRef.updateAndGet( previous -> updateAndSort( previous, element, AtomicSortedList::concat, comparator ) );
    }

    /**
     * Removes an element to this list, preserving sort order.
     *
     * @param element an element to remove from this list
     */
    public void remove( T element )
    {
        elementsRef.updateAndGet( previous -> updateAndSort( previous, element, AtomicSortedList::subtract, comparator ) );
    }

    /**
     * Returns an unmodifiable snapshot of this list.
     *
     * @return an unmodifiable snapshot of this list
     */
    public List<T> snapshot()
    {
        return elementsRef.get();
    }

    private static <T> List<T> updateAndSort( List<T> previous, T element, BiFunction<Stream<T>, T, Stream<T>> update,
                                              Comparator<T> comparator )
    {
        return update.apply( previous.stream(), element ).sorted( comparator ).collect( Collectors.toUnmodifiableList() );
    }

    private static <T> Stream<T> subtract( final Stream<T> stream, final T element )
    {
        return stream.filter( e -> e != element );
    }

    private static <T> Stream<T> concat( final Stream<T> stream, final T element )
    {
        return Stream.concat( stream, Stream.of( element ) );
    }
}
