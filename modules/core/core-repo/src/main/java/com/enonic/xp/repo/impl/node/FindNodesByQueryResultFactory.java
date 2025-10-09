package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class FindNodesByQueryResultFactory
{
    static FindNodesByQueryResult create( final SearchResult result )
    {
        final FindNodesByQueryResult.Builder resultBuilder = FindNodesByQueryResult.create().
            totalHits( result.getTotalHits() ).
            aggregations( result.getAggregations() ).
            suggestions( result.getSuggestions() );

        for ( final SearchHit hit : result.getHits() )
        {
            final NodeHit.Builder nodeHit = NodeHit.create().
                nodeId( NodeId.from( hit.getId() ) ).
                score( hit.getScore() ).
                explanation( hit.getExplanation() ).
                highlight( hit.getHighlightedProperties() ).
                sort( hit.getSortValues() );

            final String nodePath = (String) hit.getReturnValues().getSingleValue( NodeIndexPath.PATH.getPath() );

            if ( nodePath != null )
            {
                nodeHit.nodePath( new NodePath( nodePath ) );
            }

            resultBuilder.addNodeHit( nodeHit.build() );

        }

        return resultBuilder.build();
    }
}
