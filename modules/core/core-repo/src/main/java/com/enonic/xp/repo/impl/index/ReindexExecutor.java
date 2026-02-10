package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ReindexListener;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.IndexDataService;
import com.enonic.xp.repository.RepositoryId;

public class ReindexExecutor
{
    private final Branches branches;

    private final RepositoryId repositoryId;

    private final NodeSearchService nodeSearchService;

    private final NodeVersionService nodeVersionService;

    private final IndexDataService indexDataService;

    private final ReindexListener listener;

    private ReindexExecutor( final Builder builder )
    {
        branches = builder.branches;
        repositoryId = builder.repositoryId;
        nodeSearchService = builder.nodeSearchService;
        nodeVersionService = builder.nodeVersionService;
        indexDataService = builder.indexDataService;
        listener = builder.listener;
    }

    public ReindexResult execute()
    {
        final ReindexResult.Builder builder = ReindexResult.create();
        final long start = System.currentTimeMillis();
        builder.startTime( Instant.ofEpochMilli( start ) );
        builder.branches( this.branches );
        builder.repositoryId( this.repositoryId );

        if ( listener != null )
        {
            listener.totalBranches( this.branches.getSize() );
        }

        for ( final Branch branch : this.branches )
        {
            doReindexBranchNew( repositoryId, builder, branch );
        }

        final long stop = System.currentTimeMillis();
        builder.endTime( Instant.ofEpochMilli( stop ) );
        builder.duration( Duration.ofMillis( stop - start  ) );

        return builder.build();
    }

    private void doReindexBranchNew( final RepositoryId repositoryId, final ReindexResult.Builder builder, final Branch branch )
    {
        final NodeBranchEntries nodeBranchEntries = NodeBranchQueryResultFactory.create( this.nodeSearchService.query(
            NodeBranchQuery.create()
                .query( QueryExpr.from(
                    CompareExpr.create( FieldExpr.from( BranchIndexPath.BRANCH_NAME.getPath() ), CompareExpr.Operator.EQ,
                                        ValueExpr.string( branch.getValue() ) ) ) )
                .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                .build(), this.repositoryId ) );

        if ( listener != null )
        {
            listener.branch( repositoryId, branch, nodeBranchEntries.getSize() );
        }

        final InternalContext context = InternalContext.create( ContextAccessor.current() ).
            repositoryId( repositoryId ).
            branch( branch ).
            build();
        for ( final NodeBranchEntry nodeBranchEntry : nodeBranchEntries )
        {
            final NodeStoreVersion nodeVersion = this.nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context );

            final Node node = NodeFactory.create( nodeVersion, nodeBranchEntry );

            this.indexDataService.store( node, context );

            builder.add( node.id() );

            if ( listener != null )
            {
                listener.branchEntry();
            }
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branches branches;

        private RepositoryId repositoryId;

        private NodeSearchService nodeSearchService;

        private NodeVersionService nodeVersionService;

        private IndexDataService indexDataService;

        private ReindexListener listener;

        private Builder()
        {
        }

        public Builder branches( final Branches val )
        {
            branches = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder nodeSearchService( final NodeSearchService val )
        {
            nodeSearchService = val;
            return this;
        }

        public Builder nodeVersionService( final NodeVersionService val )
        {
            nodeVersionService = val;
            return this;
        }

        public Builder indexDataService( final IndexDataService val )
        {
            indexDataService = val;
            return this;
        }

        public Builder listener( final ReindexListener val )
        {
            listener = val;
            return this;
        }

        public ReindexExecutor build()
        {
            return new ReindexExecutor( this );
        }
    }
}
