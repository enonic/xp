package com.enonic.xp.resource;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
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

    public static ResourceKeys from( final Collection<? extends ResourceKey> resourceKeys )
    {
        return new ResourceKeys( ImmutableList.copyOf( resourceKeys ) );
    }

    public static ResourceKeys from( final String... resourceKeys )
    {
        return new ResourceKeys( parseResourceKeys( resourceKeys ) );
    }

    public static ResourceKeys empty()
    {
        return new ResourceKeys( ImmutableList.<ResourceKey>of() );
    }

    private static ImmutableList<ResourceKey> parseResourceKeys( final String... resourceKeys )
    {
        final Collection<String> list = Lists.newArrayList( resourceKeys );
        final Collection<ResourceKey> resourceKeyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableList.copyOf( resourceKeyList );
    }

    private final static class ParseFunction
        implements Function<String, ResourceKey>
    {
        @Override
        public ResourceKey apply( final String value )
        {
            return ResourceKey.from( value );
        }
    }

}
