package com.enonic.xp.api;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class ApiMountDescriptors
    extends AbstractImmutableEntityList<ApiMountDescriptor>
{
    private ApiMountDescriptors( final ImmutableList<ApiMountDescriptor> list )
    {
        super( list );
    }

    public static ApiMountDescriptors empty()
    {
        return new ApiMountDescriptors( ImmutableList.of() );
    }

    public static ApiMountDescriptors from( final ApiMountDescriptor... apiMountDescriptors )
    {
        return new ApiMountDescriptors( ImmutableList.copyOf( apiMountDescriptors ) );
    }

    public static ApiMountDescriptors from( final Iterable<? extends ApiMountDescriptor> apiDescriptors )
    {
        return new ApiMountDescriptors( ImmutableList.copyOf( apiDescriptors ) );
    }
}
