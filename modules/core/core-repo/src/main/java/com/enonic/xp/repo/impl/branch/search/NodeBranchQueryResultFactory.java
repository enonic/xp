package com.enonic.xp.repo.impl.branch.search;

import com.enonic.xp.repo.impl.branch.storage.NodeBranchVersionFactory;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeBranchQueryResultFactory
{
    public static NodeBranchQueryResult create( final SearchResult searchResult )
    {
        final NodeBranchQueryResult.Builder builder = NodeBranchQueryResult.create();

        for ( final SearchHit result : searchResult.getResults() )
        {
            builder.add( NodeBranchVersionFactory.create( result.getReturnValues() ) );
        }

        return builder.build();
    }
}
