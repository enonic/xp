package com.enonic.xp.descriptor;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class Descriptors<T extends Descriptor>
    extends AbstractImmutableEntityList<T>
{
    private static final Descriptors<Descriptor> EMPTY = new Descriptors<>( ImmutableList.of() );

    private Descriptors( final Iterable<? extends T> descriptors )
    {
        super( ImmutableList.copyOf( descriptors ) );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Descriptor> Descriptors<T> empty()
    {
        return (Descriptors<T>) EMPTY;
    }

    @SafeVarargs
    public static <T extends Descriptor> Descriptors<T> from( final T... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Descriptor> Descriptors<T> from( final Iterable<? extends T> descriptors )
    {
        return descriptors instanceof Descriptors<? extends T>
            ? (Descriptors<T>) descriptors
            : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static <T extends Descriptor> Collector<T, ?, Descriptors<T>> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Descriptors::fromInternal );
    }

    private static <T extends Descriptor> Descriptors<T> fromInternal( final ImmutableList<T> list )
    {
        return list.isEmpty() ? empty() : new Descriptors<>( list );
    }
}
