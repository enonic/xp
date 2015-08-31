package com.enonic.wem.repo.internal.storage.branch;

import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;

class NodeBranchQueryResultFactory
{

    static NodeBranchQueryResult create( final SearchResult searchResult )
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
