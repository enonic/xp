package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

final class DefaultQueryParamsStrategy
    implements Supplier<String>
{
    private final Multimap<String, String> queryParams;

    DefaultQueryParamsStrategy()
    {
        queryParams = LinkedListMultimap.create();
    }

    @Override
    public String get()
    {
        final StringBuilder path = new StringBuilder();
        UrlBuilderHelper.appendParams( path, queryParams.entries() );
        return path.toString();
    }

    public void put( final String key, final String value )
    {
        this.queryParams.put( key, value );
    }

    public void putNotNull( final String key, final String value )
    {
        if ( value != null )
        {
            this.queryParams.put( key, value );
        }
    }

    public void putAll( final String key, final Iterable<String> values )
    {
        this.queryParams.putAll( key, values );
    }
}
