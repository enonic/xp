package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class FindNodesByQueryResultFactory
{
    static FindNodesByQueryResult create( final SearchResult result )
    {
        final FindNodesByQueryResult.Builder resultBuilder = FindNodesByQueryResult.create().
            hits( result.getNumberOfHits() ).
            totalHits( result.getTotalHits() ).
            aggregations( result.getAggregations() );

        for ( final SearchHit resultEntry : result.getHits() )
        {
            resultBuilder.addNodeHit( new NodeHit( NodeId.from( resultEntry.getId() ), resultEntry.getScore() ) );
        }

        return resultBuilder.build();
    }
}
