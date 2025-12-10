package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

class NodeVersionDiffResultFactory
{

    public static NodeVersionDiffResult create( final SearchResult result )
    {
        if ( result.isEmpty() )
        {
            return NodeVersionDiffResult.create().
                totalHits( result.getTotalHits() ).
                build();
        }

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        builder.totalHits( result.getTotalHits() );

        for ( final SearchHit hit : result.getHits() )
        {
            builder.add( NodeId.from( hit.getReturnValues().getStringValue( VersionIndexPath.NODE_ID ) ) );
        }

        return builder.build();
    }
}
