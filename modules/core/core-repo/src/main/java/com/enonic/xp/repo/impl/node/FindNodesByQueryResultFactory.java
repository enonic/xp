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
            aggregations( result.getAggregations() ).
            suggestions( result.getSuggestions() );

        for ( final SearchHit hit : result.getHits() )
        {
            resultBuilder.addNodeHit( NodeHit.create().
                nodeId( NodeId.from( hit.getId() ) ).
                score( hit.getScore() ).
                explanation( hit.getExplanation() ).
                build() );
        }

        return resultBuilder.build();
    }
}
