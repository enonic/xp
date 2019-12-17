package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionQueryResultFactory;

public class FindNodeVersionsCommand
{
    private final NodeSearchService nodeSearchService;

    private final NodeVersionQuery query;

    private FindNodeVersionsCommand( Builder builder )
    {
        this.query = builder.query;
        this.nodeSearchService = builder.nodeSearchService;
    }

    public NodeVersionQueryResult execute()
    {
        final SearchResult result = this.nodeSearchService.query( query, SingleRepoStorageSource.create(
            ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) );

        if ( result.isEmpty() )
        {
            return NodeVersionQueryResult.empty( result.getTotalHits() );
        }

        return NodeVersionQueryResultFactory.create( query, result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeVersionQuery query;

        private NodeSearchService nodeSearchService;

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

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeSearchService, "SearchService must be set" );
            Preconditions.checkNotNull( this.query, "Query must be set" );
        }

        public FindNodeVersionsCommand build()
        {
            this.validate();
            return new FindNodeVersionsCommand( this );
        }
    }
}
