package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class FindNodePathsByQueryResultFactory
{
    static FindNodePathsByQueryResult create( final SearchResult result )
    {
        final FindNodePathsByQueryResult.Builder resultBuilder = FindNodePathsByQueryResult.create();

        for ( final SearchHit hit : result.getHits() )
        {
            resultBuilder.path( (String)hit.getField( NodeIndexPath.PATH.toString() ).getSingleValue() );
        }

        return resultBuilder.build();
    }
}
