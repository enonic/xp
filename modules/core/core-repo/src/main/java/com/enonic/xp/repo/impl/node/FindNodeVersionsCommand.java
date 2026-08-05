package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.version.NodeVersionFactory;
import com.enonic.xp.repo.impl.version.search.NodeVersionQueryResultFactory;
import com.enonic.xp.repo.impl.version.search.SpiVersionQueryFactory;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.VersionQueryResult;

import static java.util.Objects.requireNonNull;

public class FindNodeVersionsCommand
{
    private final NodeSearchService nodeSearchService;

    private final NodeStore nodeStore;

    private final NodeVersionQuery query;

    private FindNodeVersionsCommand( Builder builder )
    {
        this.query = builder.query;
        this.nodeSearchService = builder.nodeSearchService;
        this.nodeStore = builder.nodeStore;
    }

    public NodeVersionQueryResult execute()
    {
        if ( this.nodeStore.supportsVersionQueries() )
        {
            final VersionQueryResult result =
                this.nodeStore.findVersions( ContextAccessor.current().getRepositoryId(), SpiVersionQueryFactory.create( query ) );

            return NodeVersionQueryResult.create()
                .totalHits( result.totalHits() )
                .entityVersions( result.versions().stream().map( NodeVersionFactory::fromRecord ).collect( NodeVersions.collector() ) )
                .build();
        }

        final SearchResult result = this.nodeSearchService.query( query, ContextAccessor.current().getRepositoryId() );
        return NodeVersionQueryResultFactory.create( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeVersionQuery query;

        private NodeSearchService nodeSearchService;

        private NodeStore nodeStore;

        private Builder()
        {
        }

        public Builder query( final NodeVersionQuery nodeVersionQuery )
        {
            this.query = nodeVersionQuery;
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

        public FindNodeVersionsCommand build()
        {
            this.validate();
            return new FindNodeVersionsCommand( this );
        }
    }
}
