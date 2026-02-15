package com.enonic.xp.portal.url;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

class QueryParamsBuilder
{
    private final Map<String, List<String>> queryParams = new LinkedHashMap<>();

    void setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
    {
        for ( Map.Entry<String, ? extends Collection<String>> stringEntry : queryParams.entrySet() )
        {
            this.queryParams.put( stringEntry.getKey(), new ArrayList<>( stringEntry.getValue() ) );
        }
    }

    void addQueryParam( final String key, final String value )
    {
        this.queryParams.computeIfAbsent( key, k -> new ArrayList<>() ).add( value );
    }

    Map<String, List<String>> build()
    {
        final ImmutableMap.Builder<String, List<String>> queryParamsBuilder = ImmutableMap.builder();
        for ( Map.Entry<String, List<String>> e : queryParams.entrySet() )
        {
            queryParamsBuilder.put( e.getKey(), ImmutableList.copyOf( e.getValue() ) );
        }
        return queryParamsBuilder.build();
    }
}
