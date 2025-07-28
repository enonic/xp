package com.enonic.xp.region;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PartDescriptors
    extends AbstractImmutableEntityList<PartDescriptor>
{
    private static final PartDescriptors EMPTY = new PartDescriptors( ImmutableList.of() );

    private PartDescriptors( final ImmutableList<PartDescriptor> list )
    {
        super( list );
    }

    public static PartDescriptors empty()
    {
        return EMPTY;
    }

    public static PartDescriptors from( final PartDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static PartDescriptors from( final Iterable<PartDescriptor> descriptors )
    {
        return descriptors instanceof PartDescriptors d ? d : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static Collector<PartDescriptor, ?, PartDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), PartDescriptors::fromInternal );
    }

    private static PartDescriptors fromInternal( final ImmutableList<PartDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new PartDescriptors( list );
    }
}
