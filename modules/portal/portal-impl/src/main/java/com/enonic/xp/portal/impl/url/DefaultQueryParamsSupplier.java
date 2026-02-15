package com.enonic.xp.portal.impl.url;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

final class DefaultQueryParamsSupplier
    implements Supplier<String>
{
    private final Map<String, List<String>> queryParams;

    DefaultQueryParamsSupplier()
    {
        queryParams = new LinkedHashMap<>();
    }

    @Override
    public String get()
    {
        final StringBuilder path = new StringBuilder();
        UrlBuilderHelper.appendParams( path, queryParams );
        return path.toString();
    }

    public void params( final Map<String, List<String>> map )
    {
        map.forEach( this::param );
    }

    public void params( final ListMultimap<String, String> multimap )
    {
        this.queryParams.putAll( Multimaps.asMap( multimap ) );
    }

    public void param( final String key, final List<String> values )
    {
        this.queryParams.put( key, values );
    }

    public void param( final String key, final String value )
    {
        this.queryParams.put( key, List.of( value ) );
    }
}
