package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ReindexListener;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.node.executor.BatchedExecutor;
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

    private final static int BATCH_SIZE = 1000;

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

        final Stopwatch started = Stopwatch.createStarted();
        for ( final Branch branch : this.branches )
        {
            doReindexBranchNew( repositoryId, builder, branch );
        }

        final long stop = System.currentTimeMillis();
        builder.endTime( Instant.ofEpochMilli( stop ) );
        builder.duration( Duration.ofMillis( start - stop ) );

        return builder.build();
    }

    private void doReindexBranchNew( final RepositoryId repositoryId, final ReindexResult.Builder builder, final Branch branch )
    {
        final BatchedExecutor<List<NodeBranchEntry>> executor = new BatchedExecutor<>( GetBranchDataCommand.create().
            branch( branch ).
            repositoryId( repositoryId ).
            nodeSearchService( this.nodeSearchService ).
            build(), BATCH_SIZE );

        final long total = executor.getTotalHits();

        if ( listener != null )
        {
            listener.branch( repositoryId, branch, total );
        }

        while ( executor.hasMore() )
        {
            final List<NodeBranchEntry> result = executor.execute();

            for ( final NodeBranchEntry nodeBranchEntry : result )
            {
                final InternalContext context = InternalContext.create( ContextAccessor.current() ).
                    repositoryId( repositoryId ).
                    branch( branch ).
                    build();

                final NodeVersion nodeVersion = this.nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context );

                final Node node = NodeFactory.create( nodeVersion, nodeBranchEntry );

                this.indexDataService.store( node, context );

                builder.add( node.id() );

                if ( listener != null )
                {
                    listener.branchEntry( nodeBranchEntry );
                }
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
