package com.enonic.xp.resource;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ResourceKeys
    extends AbstractImmutableEntityList<ResourceKey>
{
    private ResourceKeys( final ImmutableList<ResourceKey> list )
    {
        super( list );
    }

    public static ResourceKeys from( final ResourceKey... resourceKeys )
    {
        return new ResourceKeys( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final Iterable<? extends ResourceKey> resourceKeys )
    {
        return new ResourceKeys( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final Iterator<? extends ResourceKey> resourceKeys )
    {
        return new ResourceKeys( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final String... resourceKeys )
    {
        return new ResourceKeys( parseResourceKeys( resourceKeys ) );
    }

    public static ResourceKeys empty()
    {
        return new ResourceKeys( ImmutableList.of() );
    }

    private static ImmutableList<ResourceKey> parseResourceKeys( final String... resourceKeys )
    {
        return Arrays.stream( resourceKeys ).map( ResourceKey::from ).collect( ImmutableList.toImmutableList() );
    }
}
