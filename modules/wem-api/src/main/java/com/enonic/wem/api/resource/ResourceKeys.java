package com.enonic.wem.api.resource;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ResourceKeys
    extends AbstractImmutableEntityList<ResourceKey>
{
    private ResourceKeys( final ImmutableList<ResourceKey> list )
    {
        super( list );
    }

    public <T> Collection<T> transform( final Function<ResourceKey, T> function )
    {
        final Collection<T> values = Collections2.transform( this.list, function );
        return Collections2.filter( values, Predicates.notNull() );
    }

    public static ResourceKeys from( final ResourceKey... keys )
    {
        return new ResourceKeys( ImmutableList.copyOf( keys ) );
    }

    public static ResourceKeys from( final Iterable<? extends ResourceKey> keys )
    {
        return new ResourceKeys( ImmutableList.copyOf( keys ) );
    }

    public static ResourceKeys from( final String... keys )
    {
        return new ResourceKeys( parseKeys( keys ) );
    }

    public static ResourceKeys empty()
    {
        return new ResourceKeys( ImmutableList.<ResourceKey>of() );
    }

    private static ImmutableList<ResourceKey> parseKeys( final String... keys )
    {
        final Collection<String> list = Lists.newArrayList( keys );
        final Collection<ResourceKey> keyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableList.copyOf( keyList );
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
