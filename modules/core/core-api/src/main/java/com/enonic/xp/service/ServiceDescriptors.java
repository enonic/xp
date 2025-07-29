package com.enonic.xp.service;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ServiceDescriptors
    extends AbstractImmutableEntityList<ServiceDescriptor>
{
    private static final ServiceDescriptors EMPTY = new ServiceDescriptors( ImmutableList.of() );

    private ServiceDescriptors( final ImmutableList<ServiceDescriptor> list )
    {
        super( list );
    }

    public static ServiceDescriptors empty()
    {
        return EMPTY;
    }

    public static ServiceDescriptors from( final ServiceDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static ServiceDescriptors from( final Iterable<ServiceDescriptor> descriptors )
    {
        return descriptors instanceof ServiceDescriptors d ? d : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static Collector<ServiceDescriptor, ?, ServiceDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ServiceDescriptors::fromInternal );
    }

    private static ServiceDescriptors fromInternal( final ImmutableList<ServiceDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new ServiceDescriptors( list );
    }
}
