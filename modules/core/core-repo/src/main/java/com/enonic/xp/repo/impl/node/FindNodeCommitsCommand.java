package com.enonic.xp.repo.impl.node;

import java.util.List;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.repo.impl.commit.search.NodeCommitQueryResultFactory;
import com.enonic.xp.repo.impl.commit.storage.NodeCommitEntryFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.SearchResult;

import static java.util.Objects.requireNonNull;

public class FindNodeCommitsCommand
{
    private final NodeSearchService nodeSearchService;

    private final NodeStore nodeStore;

    private final NodeCommitQuery query;

    private FindNodeCommitsCommand( Builder builder )
    {
        this.query = builder.query;
        this.nodeSearchService = builder.nodeSearchService;
        this.nodeStore = builder.nodeStore;
    }

    public NodeCommitQueryResult execute()
    {
        if ( this.nodeStore.supportsVersionQueries() )
        {
            validateSpiQueryShape();

            final List<CommitRecord> commits = this.nodeStore.findCommits( ContextAccessor.current().getRepositoryId() );

            return NodeCommitQueryResult.create()
                .totalHits( commits.size() )
                .nodeCommitEntries(
                    commits.stream().map( NodeCommitEntryFactory::fromRecord ).collect( NodeCommitEntries.collector() ) )
                .build();
        }

        final SearchResult result = this.nodeSearchService.query( query, ContextAccessor.current().getRepositoryId() );
        return NodeCommitQueryResultFactory.create( result );
    }

    /**
     * The SPI surface serves the one enumerated production shape (RepoDumper: all commits,
     * size -1, no predicates) — anything else must fail loudly, never silently drop.
     */
    private void validateSpiQueryShape()
    {
        if ( query.getQuery() != null )
        {
            throw new IllegalArgumentException( "Unsupported commit query construct: query expression " + query.getQuery() );
        }
        if ( query.getQueryFilters().isNotEmpty() || query.getPostFilters().isNotEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported commit query construct: filters" );
        }
        if ( query.getAggregationQueries().isNotEmpty() || query.getSuggestionQueries().isNotEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported commit query construct: aggregations/suggestions" );
        }
        if ( !query.getOrderBys().isEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported commit query construct: ordering " + query.getOrderBys() );
        }
        if ( query.getFrom() != 0 || query.getSize() != NodeSearchService.GET_ALL_SIZE_FLAG )
        {
            throw new IllegalArgumentException(
                "Unsupported commit query paging: from=" + query.getFrom() + ", size=" + query.getSize() );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeCommitQuery query;

        private NodeSearchService nodeSearchService;

        private NodeStore nodeStore;

        private Builder()
        {
        }

        public Builder query( final NodeCommitQuery nodeCommitQuery )
        {
            this.query = nodeCommitQuery;
            return this;
        }

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        public Builder nodeStore( final NodeStore nodeStore )
        {
            this.nodeStore = nodeStore;
            return this;
        }

        private void validate()
        {
            requireNonNull( this.nodeSearchService );
            requireNonNull( this.nodeStore );
            requireNonNull( this.query, "query is required" );
        }

        public FindNodeCommitsCommand build()
        {
            this.validate();
            return new FindNodeCommitsCommand( this );
        }
    }
}
