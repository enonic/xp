package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.RepositoryId;

class FindNodesByMultiRepoQueryResultFactory
{
    static FindNodesByMultiRepoQueryResult create( final SearchResult result )
    {
        final FindNodesByMultiRepoQueryResult.Builder resultBuilder = FindNodesByMultiRepoQueryResult.create().
            totalHits( result.getTotalHits() ).
            aggregations( result.getAggregations() ).
            suggestions( result.getSuggestions() );

        for ( final SearchHit hit : result.getHits() )
        {
            resultBuilder.addNodeHit( toMultiRepoNodeHit( hit ) );
        }

        return resultBuilder.build();
    }

    private static MultiRepoNodeHit toMultiRepoNodeHit( final SearchHit hit )
    {
        return MultiRepoNodeHit.create().
            branch( Branch.from( hit.getIndexType() ) ).
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
        final String repoName = indexName.substring( SearchStorageName.STORAGE_INDEX_PREFIX.length() + SearchStorageName.DIVIDER.length() );
        return RepositoryId.from( repoName );
    }
}
