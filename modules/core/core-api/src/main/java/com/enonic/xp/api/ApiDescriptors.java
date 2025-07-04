package com.enonic.xp.api;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ApiDescriptors
    extends AbstractImmutableEntityList<ApiDescriptor>
{
    private static final ApiDescriptors EMPTY = new ApiDescriptors( ImmutableList.of() );

    private ApiDescriptors( final ImmutableList<ApiDescriptor> list )
    {
        super( list );
    }

    public static ApiDescriptors empty()
    {
        return EMPTY;
    }

    public static ApiDescriptors from( final ApiDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static ApiDescriptors from( final Iterable<ApiDescriptor> descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    private static ApiDescriptors fromInternal( final ImmutableList<ApiDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new ApiDescriptors( list );
    }

    public static Collector<ApiDescriptor, ?, ApiDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ApiDescriptors::fromInternal );
    }
}
