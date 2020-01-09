package com.enonic.xp.descriptor;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

public final class Descriptors<T extends Descriptor>
    extends AbstractImmutableEntityList<T>
{
    private Descriptors( final Iterable<? extends T> descriptors )
    {
        super( ImmutableList.copyOf( descriptors ) );
    }

    public Descriptors<T> filter( final ApplicationKey key )
    {
        return filter( t -> t.getApplicationKey().equals( key ) );
    }

    public Descriptors<T> filter( final Predicate<T> predicate )
    {
        return from( stream().filter( predicate ).collect( ImmutableList.toImmutableList() ) );
    }

    public static <T extends Descriptor> Descriptors<T> empty()
    {
        return from();
    }

    @SafeVarargs
    public static <T extends Descriptor> Descriptors<T> from( final T... descriptors )
    {
        return from( ImmutableList.copyOf( descriptors ) );
    }

    public static <T extends Descriptor> Descriptors<T> from( final Iterable<? extends T> descriptors )
    {
        return new Descriptors<>( descriptors );
    }
}
