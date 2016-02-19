package com.enonic.xp.site.mapping;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class ControllerMappingDescriptors
    extends AbstractImmutableEntityList<ControllerMappingDescriptor>
{
    private ControllerMappingDescriptors( final ImmutableList<ControllerMappingDescriptor> list )
    {
        super( list );
    }

    public static ControllerMappingDescriptors empty()
    {
        final ImmutableList<ControllerMappingDescriptor> list = ImmutableList.of();
        return new ControllerMappingDescriptors( list );
    }

    public static ControllerMappingDescriptors from( final ControllerMappingDescriptor... ControllerMappingDescriptors )
    {
        return new ControllerMappingDescriptors( ImmutableList.copyOf( ControllerMappingDescriptors ) );
    }

    public static ControllerMappingDescriptors from( final Iterable<? extends ControllerMappingDescriptor> ControllerMappingDescriptors )
    {
        return new ControllerMappingDescriptors( ImmutableList.copyOf( ControllerMappingDescriptors ) );
    }

}
