package com.enonic.xp.descriptor;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class DescriptorKeys
    extends AbstractImmutableEntitySet<DescriptorKey>
{
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
        return from( stream().filter( predicate ).collect( Collectors.toList() ) );
    }

    public DescriptorKeys concat( final DescriptorKeys keys )
    {
        return from( ImmutableSet.<DescriptorKey>builder().addAll( this ).addAll( keys ).build() );
    }

    public static DescriptorKeys empty()
    {
        return from( ImmutableSet.of() );
    }

    public static DescriptorKeys from( final DescriptorKey... keys )
    {
        return from( ImmutableSet.copyOf( keys ) );
    }

    public static DescriptorKeys from( final Iterable<DescriptorKey> keys )
    {
        return new DescriptorKeys( ImmutableSet.copyOf( keys ) );
    }
}
