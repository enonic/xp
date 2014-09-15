package com.enonic.wem.core.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.google.common.collect.Maps;

import com.enonic.wem.core.index.result.SearchResultField;

public class SearchResultFieldsFactory
{

    public static Map<String, SearchResultField> create( final SearchHit hit )
    {
        final Map<String, SearchResultField> resultFieldMap = Maps.newHashMap();

        final Map<String, SearchHitField> hitFieldMap = hit.getFields();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final SearchHitField hitField = hitFieldMap.get( fieldName );
            resultFieldMap.put( fieldName, new SearchResultField( hitField.name(), hitField.values() ) );
        }

        return resultFieldMap;
    }

    public static Map<String, SearchResultField> create( final GetResponse getResponse )
    {
        final Map<String, SearchResultField> resultFieldMap = Maps.newHashMap();

        final Map<String, GetField> hitFieldMap = getResponse.getFields();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final GetField getField = hitFieldMap.get( fieldName );

            resultFieldMap.put( fieldName, new SearchResultField( getField.getName(), getField.getValues() ) );
        }

        return resultFieldMap;
    }

}
