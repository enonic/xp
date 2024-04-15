package com.enonic.xp.api;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ApiDescriptors
    extends AbstractImmutableEntityList<ApiDescriptor>
{
    private ApiDescriptors( final ImmutableList<ApiDescriptor> list )
    {
        super( list );
    }

    public static ApiDescriptors empty()
    {
        return new ApiDescriptors( ImmutableList.of() );
    }

    public static ApiDescriptors from( final ApiDescriptor... descriptors )
    {
        return from( ImmutableList.copyOf( descriptors ) );
    }

    public static ApiDescriptors from( final Iterable<ApiDescriptor> descriptors )
    {
        return new ApiDescriptors( ImmutableList.copyOf( descriptors ) );
    }
}
