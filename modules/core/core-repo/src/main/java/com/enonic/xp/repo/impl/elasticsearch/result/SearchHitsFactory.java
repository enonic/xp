package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.search.result.SearchHit;

public class SearchHitsFactory
{
    public static List<SearchHit> create( final org.elasticsearch.search.SearchHits searchHits, ReturnFields returnFields )
    {
        final List<SearchHit> builder = new ArrayList<>();

        for ( final org.elasticsearch.search.SearchHit hit : searchHits )
        {
            final SearchHit.Builder hitBuilder = SearchHit.create()
                .id( hit.id() )
                .score( hit.score() )
                .indexName( hit.index() )
                .indexType( hit.type() )
                .returnValues( createReturnValues( hit, returnFields ) )
                .sortValues( SortValuesPropertyFactory.create( hit.sortValues() ) )
                .highlightedFields( HighlightedPropertiesFactory.create( hit.highlightFields() ) )
                .explanation( SearchExplanationFactory.create( hit ) );

            builder.add( hitBuilder.build() );
        }

        return builder;
    }

    public static ReturnValues createReturnValues( final org.elasticsearch.search.SearchHit hit, final ReturnFields returnFields )
    {
        final Map<String, Object> hitFieldMap = hit.sourceAsMap();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( String returnFieldName : returnFields.getReturnFieldNames() )
        {
            final Object o = hitFieldMap.get( returnFieldName );
            if ( o != null )
            {
                builder.add( returnFieldName, o );
            }
        }

        return builder.build();
    }
}
