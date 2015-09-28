package com.enonic.xp.resource;

import java.util.function.Predicate;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;


@Beta
public final class Resources
    extends AbstractImmutableEntityList<Resource>
{
    private final ImmutableMap<ResourceKey, Resource> map;

    private Resources( final ImmutableList<Resource> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, Resource::getKey );
    }

    public ResourceKeys getResourceKeys()
    {
        return ResourceKeys.from( map.keySet() );
    }

    public Resource getResource( final ResourceKey ResourceKey )
    {
        return map.get( ResourceKey );
    }

    public Resources filter( Predicate<Resource> predicate )
    {
        final ImmutableList<Resource> resourceList = this.list.stream().
            filter( predicate ).
            collect( collectingAndThen( toList(), ImmutableList::copyOf ) );
        return new Resources( resourceList );
    }

    public static Resources empty()
    {
        final ImmutableList<Resource> list = ImmutableList.of();
        return new Resources( list );
    }

    public static Resources from( final Resource... resources )
    {
        return new Resources( ImmutableList.copyOf( resources ) );
    }

    public static Resources from( final Iterable<? extends Resource> resources )
    {
        return new Resources( ImmutableList.copyOf( resources ) );
    }

}
