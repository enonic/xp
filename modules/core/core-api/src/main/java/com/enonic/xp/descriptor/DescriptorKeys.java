package com.enonic.xp.descriptor;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class DescriptorKeys
    extends AbstractImmutableEntitySet<DescriptorKey>
{
    private DescriptorKeys( final Iterable<DescriptorKey> keys )
    {
        super( ImmutableSet.copyOf( keys ) );
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
        return from( Iterables.concat( this, keys ) );
    }

    public static DescriptorKeys empty()
    {
        return from();
    }

    public static DescriptorKeys from( final DescriptorKey... keys )
    {
        return from( Lists.newArrayList( keys ) );
    }

    public static DescriptorKeys from( final Iterable<DescriptorKey> keys )
    {
        return new DescriptorKeys( keys );
    }
}
