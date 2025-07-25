package com.enonic.xp.repo.impl.elasticsearch.result;

import org.apache.lucene.search.Explanation;
import org.elasticsearch.search.SearchHit;

import com.enonic.xp.query.QueryExplanation;

class SearchExplanationFactory
{
    static QueryExplanation create( final SearchHit searchHit )
    {
        final Explanation explanation = searchHit.getExplanation();

        if ( explanation != null )
        {
            return doCreate( explanation );
        }

        return null;
    }

    private static QueryExplanation doCreate( final Explanation explanation )
    {
        final QueryExplanation.Builder builder = QueryExplanation.create();

        builder.description( explanation.getDescription() );
        builder.value( explanation.getValue() );

        if ( explanation.getDetails() != null )
        {
            for ( final Explanation detail : explanation.getDetails() )
            {
                builder.addDetail( doCreate( detail ) );
            }
        }

        return builder.build();
    }
}
