package com.enonic.xp.repo.impl.index;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.node.executor.ExecutorCommand;
import com.enonic.xp.repo.impl.node.executor.ExecutorCommandResult;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.RepositoryId;

class GetBranchDataCommand
    implements ExecutorCommand<List<NodeBranchEntry>>
{
    private final Branch branch;

    private final NodeSearchService nodeSearchService;

    private final RepositoryId repositoryId;

    private final long totalHits;

    private GetBranchDataCommand( final Builder builder )
    {
        branch = builder.branch;
        nodeSearchService = builder.nodeSearchService;
        repositoryId = builder.repositoryId;
        final SearchResult result = doQuery( 0, 0 );
        this.totalHits = result.getTotalHits();
    }

    @Override
    public long getTotalHits()
    {
        return this.totalHits;
    }

    @Override
    public ExecutorCommandResult<List<NodeBranchEntry>> execute( final int from, final int size )
    {
        final SearchResult result = doQuery( from, size );

        final NodeBranchQueryResult nodeBranchEntries = NodeBranchQueryResultFactory.create( result );

        return new NodeBranchEntryResult( nodeBranchEntries.getList() );
    }

    private SearchResult doQuery( final int from, final int size )
    {
        final CompareExpr compareExpr =
            CompareExpr.create( FieldExpr.from( BranchIndexPath.BRANCH_NAME.getPath() ), CompareExpr.Operator.EQ,
                                ValueExpr.string( branch.getValue() ) );

        final Context reindexContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( this.repositoryId ).
            branch( branch ).
            build();

        return this.nodeSearchService.query( NodeBranchQuery.create().
            query( QueryExpr.from( compareExpr ) ).
            from( from ).
            size( size ).
            build(), SingleRepoStorageSource.create( reindexContext.getRepositoryId(), SingleRepoStorageSource.Type.BRANCH ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branch branch;

        private NodeSearchService nodeSearchService;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder branch( final Branch val )
        {
            branch = val;
            return this;
        }

        public Builder nodeSearchService( final NodeSearchService val )
        {
            nodeSearchService = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.repositoryId );
            Preconditions.checkNotNull( this.branch );
            Preconditions.checkNotNull( this.nodeSearchService );
        }

        public GetBranchDataCommand build()
        {
            validate();
            return new GetBranchDataCommand( this );
        }
    }
}
