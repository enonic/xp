package com.enonic.wem.core.elasticsearch;

import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import com.google.common.collect.Sets;

public class SearchResponseAccessor
{
    public static SearchHit getSingleHit( final SearchResponse searchResponse )
    {
        final int numberOfHits = searchResponse.getHits().hits().length;

        if ( numberOfHits == 1 )
        {
            return searchResponse.getHits().getAt( 0 );
        }

        if ( numberOfHits > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 hit, got " + numberOfHits );
        }

        return null;
    }

    public static Object getFieldValue( final SearchHit searchHit, final String fieldName )
    {
        return doGetFieldValue( searchHit, fieldName );
    }

    public static List<Object> getMultipleValues( final SearchHit searchHit, final String fieldName )
    {
        return doGetMultipleValues( searchHit, fieldName );
    }


    private static Object doGetFieldValue( final SearchHit searchHit, final String fieldName )
    {
        if ( searchHit.getFields().containsKey( fieldName ) )
        {
            return searchHit.getFields().get( fieldName ).getValue();
        }

        return null;
    }

    private static List<Object> doGetMultipleValues( final SearchHit searchHit, final String fieldName )
    {
        if ( searchHit.getFields().containsKey( fieldName ) )
        {
            return searchHit.getFields().get( fieldName ).getValues();
        }

        return null;
    }

    public static Set<Object> getFieldValues( final SearchHit[] searchHits, final String fieldName )
    {
        final Set<Object> values = Sets.newHashSet();

        for ( final SearchHit searchHit : searchHits )
        {
            final Object value = doGetFieldValue( searchHit, fieldName );

            if ( value != null )
            {
                values.add( value );
            }
        }

        return values;
    }


}
