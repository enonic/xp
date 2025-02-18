package com.enonic.xp.portal.impl.url;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

final class DefaultQueryParamsStrategy
    implements QueryParamsStrategy
{
    private final Multimap<String, String> queryParams;

    DefaultQueryParamsStrategy()
    {
        queryParams = LinkedListMultimap.create();
    }

    @Override
    public String generateQueryParams()
    {
        final StringBuilder path = new StringBuilder();
        UrlBuilderHelper.appendParams( path, queryParams.entries() );
        return path.toString();
    }

    public void putQueryParam( final String key, final String value )
    {
        this.queryParams.put( key, value );
    }

    public void ensureQueryParamNotNullThenPut( final String key, final String value )
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
