package com.enonic.xp.descriptor;

import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class DescriptorKeys
    extends AbstractImmutableEntitySet<DescriptorKey>
{
    private static final DescriptorKeys EMPTY = new DescriptorKeys( ImmutableSet.of() );

    private DescriptorKeys( final ImmutableSet<DescriptorKey> keys )
    {
        super( keys );
    }

    public DescriptorKeys filter( final ApplicationKey key )
    {
        return filter( t -> t.getApplicationKey().equals( key ) );
    }

    public DescriptorKeys filter( final Predicate<DescriptorKey> predicate )
    {
        return fromInternal( stream().filter( predicate ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public DescriptorKeys concat( final DescriptorKeys keys )
    {
        return fromInternal( ImmutableSet.<DescriptorKey>builder().addAll( this ).addAll( keys ).build() );
    }

    public static DescriptorKeys empty()
    {
        return EMPTY;
    }

    public static DescriptorKeys from( final DescriptorKey... keys )
    {
        return fromInternal( ImmutableSet.copyOf( keys ) );
    }

    public static DescriptorKeys from( final Iterable<DescriptorKey> keys )
    {
        return keys instanceof DescriptorKeys d ? d : fromInternal( ImmutableSet.copyOf( keys ) );
    }

    public static Collector<DescriptorKey, ?, DescriptorKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), DescriptorKeys::fromInternal );
    }

    private static DescriptorKeys fromInternal( final ImmutableSet<DescriptorKey> keys )
    {
        return keys.isEmpty() ? EMPTY : new DescriptorKeys( keys );
    }
}
