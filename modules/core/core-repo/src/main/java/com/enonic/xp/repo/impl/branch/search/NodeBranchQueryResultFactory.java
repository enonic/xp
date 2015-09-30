package com.enonic.xp.repo.impl.branch.search;

import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeBranchQueryResultFactory
{

    public static NodeBranchQueryResult create( final SearchResult searchResult )
    {
        final NodeBranchQueryResult.Builder builder = NodeBranchQueryResult.create();

        for ( final SearchHit result : searchResult.getResults() )
        {
            builder.add( result.getStringValue( BranchIndexPath.NODE_ID.toString() ),
                         result.getStringValue( BranchIndexPath.VERSION_ID.toString() ) );
        }

        return builder.build();
    }


}
