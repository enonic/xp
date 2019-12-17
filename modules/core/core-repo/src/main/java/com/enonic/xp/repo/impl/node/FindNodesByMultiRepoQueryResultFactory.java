package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.RepositoryId;

class FindNodesByMultiRepoQueryResultFactory
{
    static FindNodesByMultiRepoQueryResult create( final SearchResult result )
    {
        final FindNodesByMultiRepoQueryResult.Builder resultBuilder = FindNodesByMultiRepoQueryResult.create().
            hits( result.getNumberOfHits() ).
            totalHits( result.getTotalHits() ).
            aggregations( result.getAggregations() );

        for ( final SearchHit hit : result.getHits() )
        {
            resultBuilder.addNodeHit( toMultiRepoNodeHit( hit ) );
        }

        return resultBuilder.build();
    }

    private static MultiRepoNodeHit toMultiRepoNodeHit( final SearchHit hit )
    {
        return MultiRepoNodeHit.create().
            branch( Branch.from( hit.getField( NodeIndexPath.BRANCH.getPath()).getSingleValue().toString() ) ).
            repositoryId( getRepoId( hit ) ).
            nodeId( NodeId.from( hit.getId() ) ).
            score( hit.getScore() ).
            explanation( hit.getExplanation() ).
            highlight( hit.getHighlightedProperties() ).
            build();
    }

    private static RepositoryId getRepoId( final SearchHit hit )
    {
        final String indexName = hit.getIndexName();
        final String repoBranchName =
            indexName.substring( SearchStorageName.STORAGE_INDEX_PREFIX.length() + SearchStorageName.DIVIDER.length() );
        final String repoName = repoBranchName.substring( 0, repoBranchName.lastIndexOf( SearchStorageName.DIVIDER ) );
        return RepositoryId.from( repoName );
    }
}
