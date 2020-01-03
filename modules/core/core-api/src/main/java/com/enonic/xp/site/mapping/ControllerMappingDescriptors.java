package com.enonic.xp.site.mapping;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ControllerMappingDescriptors
    extends AbstractImmutableEntityList<ControllerMappingDescriptor>
{
    private ControllerMappingDescriptors( final ImmutableList<ControllerMappingDescriptor> list )
    {
        super( list );
    }

    public static ControllerMappingDescriptors empty()
    {
        return new ControllerMappingDescriptors( ImmutableList.of() );
    }

    public static ControllerMappingDescriptors from( final ControllerMappingDescriptor... controllerMappingDescriptors )
    {
        return new ControllerMappingDescriptors( ImmutableList.copyOf( controllerMappingDescriptors ) );
    }

    public static ControllerMappingDescriptors from( final Iterable<? extends ControllerMappingDescriptor> controllerMappingDescriptors )
    {
        return new ControllerMappingDescriptors( ImmutableList.copyOf( controllerMappingDescriptors ) );
    }

}
