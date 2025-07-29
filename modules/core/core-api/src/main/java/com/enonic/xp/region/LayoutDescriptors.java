package com.enonic.xp.region;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class LayoutDescriptors
    extends AbstractImmutableEntityList<LayoutDescriptor>
{
    private static final LayoutDescriptors EMPTY = new LayoutDescriptors( ImmutableList.of() );

    private LayoutDescriptors( final ImmutableList<LayoutDescriptor> list )
    {
        super( list );
    }

    public static LayoutDescriptors empty()
    {
        return EMPTY;
    }

    public static LayoutDescriptors from( final LayoutDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static LayoutDescriptors from( final Iterable<LayoutDescriptor> descriptors )
    {
        return descriptors instanceof LayoutDescriptors d ? d : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static Collector<LayoutDescriptor, ?, LayoutDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), LayoutDescriptors::fromInternal );
    }

    private static LayoutDescriptors fromInternal( final ImmutableList<LayoutDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new LayoutDescriptors( list );
    }
}
