package com.enonic.xp.repo.impl.elasticsearch.branch;

import com.enonic.xp.repo.impl.index.result.SearchResult;
import com.enonic.xp.repo.impl.index.result.SearchResultEntry;

class NodeBranchQueryResultFactory
{

    static NodeBranchQueryResult create( final SearchResult searchResult )
    {
        final NodeBranchQueryResult.Builder builder = NodeBranchQueryResult.create();

        for ( final SearchResultEntry result : searchResult.getResults() )
        {
            builder.add( result.getStringValue( BranchIndexPath.NODE_ID.toString() ),
                         result.getStringValue( BranchIndexPath.VERSION_ID.toString() ) );
        }

        return builder.build();
    }


}
