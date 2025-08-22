package com.enonic.xp.repo.impl.index;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.RepositoryId;

class GetBranchDataCommand
{
    private final Branch branch;

    private final NodeSearchService nodeSearchService;

    private final RepositoryId repositoryId;

    private GetBranchDataCommand( final Builder builder )
    {
        branch = builder.branch;
        nodeSearchService = builder.nodeSearchService;
        repositoryId = builder.repositoryId;
    }

    public NodeBranchEntries execute()
    {
        final SearchResult result = doExecute();

        return  NodeBranchQueryResultFactory.create( result );
    }

    private SearchResult doExecute()
    {
        final CompareExpr compareExpr =
            CompareExpr.create( FieldExpr.from( BranchIndexPath.BRANCH_NAME.getPath() ), CompareExpr.Operator.EQ,
                                ValueExpr.string( branch.getValue() ) );

        return this.nodeSearchService.query(
            NodeBranchQuery.create().query( QueryExpr.from( compareExpr ) ).size( NodeSearchService.GET_ALL_SIZE_FLAG ).build(),
            this.repositoryId );
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
            Objects.requireNonNull( this.nodeSearchService );
            Objects.requireNonNull( this.repositoryId, "repositoryId is required" );
            Objects.requireNonNull( this.branch, "branch is required" );
        }

        public GetBranchDataCommand build()
        {
            validate();
            return new GetBranchDataCommand( this );
        }
    }
}
