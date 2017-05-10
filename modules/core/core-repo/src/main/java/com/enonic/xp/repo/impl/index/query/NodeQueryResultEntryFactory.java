package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repository.RepositoryId;

class NodeQueryResultEntryFactory
{
    public static NodeQueryResultEntry create( final SearchHit searchHit )
    {
        return NodeQueryResultEntry.create().
            branch( Branch.from( searchHit.getIndexType() ) ).
            repositoryId( createRepoId( searchHit ) ).
            returnValues( searchHit.getReturnValues() ).
            id( NodeId.from( searchHit.getId() ) ).
            score( searchHit.getScore() ).
            build();
    }

    private static RepositoryId createRepoId( final SearchHit searchHit )
    {
        final String indexName = searchHit.getIndexName();
        final String repoName = indexName.substring( SearchStorageName.STORAGE_INDEX_PREFIX.length() + SearchStorageName.DIVIDER.length() );
        return RepositoryId.from( repoName );
    }
}
