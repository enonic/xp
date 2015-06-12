package com.enonic.xp.resource;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;


@Beta
public final class Resources
    extends AbstractImmutableEntityList<Resource>
{
    private final ImmutableMap<ResourceKey, Resource> map;

    private Resources( final ImmutableList<Resource> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToKeyFunction() );
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
        final List<Resource> resourceList = this.list.stream().
            filter( predicate ).
            collect( Collectors.toList() );
        return from( resourceList );
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

    public static Resources from( final Collection<? extends Resource> resources )
    {
        return new Resources( ImmutableList.copyOf( resources ) );
    }

    private final static class ToKeyFunction
        implements Function<Resource, ResourceKey>
    {
        @Override
        public ResourceKey apply( final Resource value )
        {
            return value.getKey();
        }
    }


}
