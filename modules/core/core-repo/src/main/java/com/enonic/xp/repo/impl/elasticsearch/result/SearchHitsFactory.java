package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.repo.impl.search.result.SearchHit;

public class SearchHitsFactory
{
    public static List<SearchHit> create( final org.elasticsearch.search.SearchHits searchHits )
    {
        final List<SearchHit> builder = new ArrayList<>();

        for ( final org.elasticsearch.search.SearchHit hit : searchHits )
        {
            final SearchHit.Builder hitBuilder = SearchHit.create().
                id( hit.id() ).
                score( hit.score() ).
                indexName( hit.index() ).
                indexType( hit.type() ).
                returnValues( ReturnValuesFactory.create( hit ) ).
                sortValues( SortValuesPropertyFactory.create( hit.sortValues() ) ).
                highlightedFields( HighlightedPropertiesFactory.create( hit.highlightFields() ) )
                .explanation( SearchExplanationFactory.create( hit ) );

            builder.add( hitBuilder.build() );
        }

        return builder;
    }
}
