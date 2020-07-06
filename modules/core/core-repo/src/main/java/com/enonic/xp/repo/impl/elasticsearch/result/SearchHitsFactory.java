package com.enonic.xp.repo.impl.elasticsearch.result;

import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;

public class SearchHitsFactory
{

    public static SearchHits create( final org.elasticsearch.search.SearchHits searchHits )
    {
        final SearchHits.Builder builder = SearchHits.create();

        for ( final org.elasticsearch.search.SearchHit hit : searchHits )
        {
            final SearchHit.Builder hitBuilder = SearchHit.create().
                id( hit.id() ).
                score( hit.score() ).
                indexName( hit.index() ).
                indexType( hit.type() ).
                returnValues( ReturnValuesFactory.create( hit ) ).
                sortValues( SortValuesPropertyFactory.create( hit.sortValues() ) ).
                highlightedFields( HighlightedPropertiesFactory.create( hit.highlightFields() ) );

            if ( hit.getExplanation() != null )
            {
                hitBuilder.explanation( SearchExplanationFactory.create( hit ) );
            }

            builder.add( hitBuilder.build() );
        }

        return builder.build();
    }
}
