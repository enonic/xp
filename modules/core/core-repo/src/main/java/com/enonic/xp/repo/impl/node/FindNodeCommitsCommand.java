package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.commit.search.NodeCommitQueryResultFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodeCommitsCommand
{
    private final NodeSearchService nodeSearchService;

    private final NodeCommitQuery query;

    private FindNodeCommitsCommand( Builder builder )
    {
        this.query = builder.query;
        this.nodeSearchService = builder.nodeSearchService;
    }

    public NodeCommitQueryResult execute()
    {
        final SearchResult result = this.nodeSearchService.query( query, SingleRepoStorageSource.create(
            ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.COMMIT ) );

        if ( result.isEmpty() )
        {
            return NodeCommitQueryResult.empty( result.getTotalHits() );
        }

        return NodeCommitQueryResultFactory.create( query, result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeCommitQuery query;

        private NodeSearchService nodeSearchService;

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

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeSearchService, "SearchService must be set" );
            Preconditions.checkNotNull( this.query, "Query must be set" );
        }

        public FindNodeCommitsCommand build()
        {
            this.validate();
            return new FindNodeCommitsCommand( this );
        }
    }
}
