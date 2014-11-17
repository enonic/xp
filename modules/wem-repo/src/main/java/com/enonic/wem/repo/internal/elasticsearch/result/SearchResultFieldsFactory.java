package com.enonic.wem.repo.internal.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;

class SearchResultFieldsFactory
{

    public static Map<String, SearchResultFieldValue> create( final SearchHit hit )
    {
        final Map<String, SearchResultFieldValue> resultFieldMap = Maps.newHashMap();

        final Map<String, SearchHitField> hitFieldMap = hit.getFields();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final SearchHitField hitField = hitFieldMap.get( fieldName );
            resultFieldMap.put( fieldName, SearchResultFieldValue.values( hitField.values() ) );
        }

        return resultFieldMap;
    }

    public static Map<String, SearchResultFieldValue> create( final GetResponse getResponse )
    {
        final Map<String, SearchResultFieldValue> resultFieldMap = Maps.newHashMap();

        final Map<String, GetField> hitFieldMap = getResponse.getFields();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final GetField getField = hitFieldMap.get( fieldName );

            resultFieldMap.put( fieldName, SearchResultFieldValue.values( getField.getValues() ) );
        }

        return resultFieldMap;
    }

}
