package com.enonic.xp.service;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ServiceDescriptors
    extends AbstractImmutableEntityList<ServiceDescriptor>
{
    private ServiceDescriptors( final ImmutableList<ServiceDescriptor> list )
    {
        super( list );
    }

    public static ServiceDescriptors empty()
    {
        final ImmutableList<ServiceDescriptor> list = ImmutableList.of();
        return new ServiceDescriptors( list );
    }

    public static ServiceDescriptors from( final ServiceDescriptor... descriptors )
    {
        return from( ImmutableList.copyOf( descriptors ) );
    }

    public static ServiceDescriptors from( final Iterable<ServiceDescriptor> descriptors )
    {
        return new ServiceDescriptors( ImmutableList.copyOf( descriptors ) );
    }
}
