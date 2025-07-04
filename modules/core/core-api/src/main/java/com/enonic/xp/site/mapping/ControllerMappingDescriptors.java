package com.enonic.xp.site.mapping;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ControllerMappingDescriptors
    extends AbstractImmutableEntityList<ControllerMappingDescriptor>
{
    private static final ControllerMappingDescriptors EMPTY = new ControllerMappingDescriptors( ImmutableList.of() );

    private ControllerMappingDescriptors( final ImmutableList<ControllerMappingDescriptor> list )
    {
        super( list );
    }

    public static ControllerMappingDescriptors empty()
    {
        return EMPTY;
    }

    public static ControllerMappingDescriptors from( final ControllerMappingDescriptor... controllerMappingDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( controllerMappingDescriptors ) );
    }

    public static ControllerMappingDescriptors from( final Iterable<? extends ControllerMappingDescriptor> controllerMappingDescriptors )
    {
        return fromInternal( ImmutableList.copyOf( controllerMappingDescriptors ) );
    }

    private static ControllerMappingDescriptors fromInternal( final ImmutableList<ControllerMappingDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new ControllerMappingDescriptors( list );
    }

    public static Collector<ControllerMappingDescriptor, ?, ControllerMappingDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ControllerMappingDescriptors::fromInternal );
    }
}
