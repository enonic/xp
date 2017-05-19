package com.enonic.xp.repo.impl.elasticsearch.result;

import org.apache.lucene.search.Explanation;
import org.elasticsearch.search.SearchHit;

import com.enonic.xp.repo.impl.search.result.SearchExplanation;

class SearchExplanationFactory
{
    static SearchExplanation create( final SearchHit searchHit )
    {
        final Explanation explanation = searchHit.getExplanation();

        return doCreate( explanation );
    }

    private static SearchExplanation doCreate( final Explanation explanation )
    {
        final SearchExplanation.Builder builder = SearchExplanation.create();

        builder.description( explanation.getDescription() );
        for ( final Explanation detail : explanation.getDetails() )
        {
            builder.addDetail( doCreate( detail ) );
        }

        return builder.build();
    }
}
