package com.enonic.xp.resource;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ResourceKeys
    extends AbstractImmutableEntityList<ResourceKey>
{
    private static final ResourceKeys EMPTY = new ResourceKeys( ImmutableList.of() );

    private ResourceKeys( final ImmutableList<ResourceKey> list )
    {
        super( list );
    }

    public static ResourceKeys empty()
    {
        return EMPTY;
    }

    public static ResourceKeys from( final ResourceKey... resourceKeys )
    {
        return fromInternal( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final Iterable<ResourceKey> resourceKeys )
    {
        return resourceKeys instanceof ResourceKeys r ? r : fromInternal( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final String... resourceKeys )
    {
        return Arrays.stream( resourceKeys ).map( ResourceKey::from ).collect( collector() );
    }

    public static Collector<ResourceKey, ?, ResourceKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), ResourceKeys::fromInternal );
    }

    private static ResourceKeys fromInternal( final ImmutableList<ResourceKey> resourceKeys )
    {
        return resourceKeys.isEmpty() ? EMPTY : new ResourceKeys( resourceKeys );
    }
}
