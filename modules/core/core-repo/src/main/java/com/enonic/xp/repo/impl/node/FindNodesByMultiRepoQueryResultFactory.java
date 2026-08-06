package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.storage.spi.SearchHit;
import com.enonic.xp.storage.spi.SearchResult;
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

    /**
     * A hit's repository — from the backend's own attribution, not by parsing an index name.
     * <p>
     * Phase 4 Gate 0(b) (nodb/BUILD-PHASE-4.md) flagged this method: it string-sliced the
     * {@code search-} prefix off {@code _index} and called the remainder a repository id. That
     * works only while a repository is exactly one index whose name embeds its id. Under NoDB it is
     * not: a hit's repository and branch ride EXPLICIT {@code _repo}/{@code _branch} document
     * fields (physical index names are generational, {@code <tenant>-<repo>+g<N>}, and DESIGN §5
     * forbids parsing one back), so the search seam reports the repository id itself here — and an
     * unconditional 7-character cut turned that into a {@code StringIndexOutOfBoundsException} for
     * any repository whose id is shorter than the prefix it was assumed to carry.
     * <p>
     * The prefix is now stripped only when it IS one. Byte-identical for the Elasticsearch path,
     * where the prefix is always present.
     */
    private static RepositoryId getRepoId( final SearchHit hit )
    {
        final String indexName = hit.getIndexName();
        final String prefix = SearchStorageName.STORAGE_INDEX_PREFIX + SearchStorageName.DIVIDER;
        return RepositoryId.from( indexName.startsWith( prefix ) ? indexName.substring( prefix.length() ) : indexName );
    }
}
