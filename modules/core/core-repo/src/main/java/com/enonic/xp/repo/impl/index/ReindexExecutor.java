package com.enonic.xp.repo.impl.index;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
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
import com.enonic.xp.task.ProgressReporter;

public class ReindexExecutor
{
    private final Branches branches;

    private final RepositoryId repositoryId;

    private final NodeSearchService nodeSearchService;

    private final NodeVersionService nodeVersionService;

    private final IndexDataService indexDataService;

    private final static int BATCH_SIZE = 1000;

    private final ProgressReporter progressReporter;

    private ReindexExecutor( final Builder builder )
    {
        branches = builder.branches;
        repositoryId = builder.repositoryId;
        nodeSearchService = builder.nodeSearchService;
        nodeVersionService = builder.nodeVersionService;
        indexDataService = builder.indexDataService;
        progressReporter = builder.progressReporter;
    }

    public ReindexResult execute()
    {
        final ReindexResult.Builder builder = ReindexResult.create();
        final long start = System.currentTimeMillis();
        builder.startTime( Instant.ofEpochMilli( start ) );
        builder.branches( this.branches );
        builder.repositoryId( this.repositoryId );

        final Stopwatch started = Stopwatch.createStarted();
        for ( final Branch branch : this.branches )
        {
            doReindexBranchNew( repositoryId, builder, branch );
        }

        progressReporter.info( "Reindexed '" + this.branches.getSize() + "' branches in '" + started.stop() );

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

        long nodeIndex = 1;
        final long total = executor.getTotalHits();
        final long logStep = total < 10 ? 1 : total < 100 ? 10 : total < 1000 ? 100 : 1000;

        progressReporter.info( "Reindexing '" + branch + "' branch in '" + repositoryId + "' repository" );

        while ( executor.hasMore() )
        {
            final List<NodeBranchEntry> result = executor.execute();

            for ( final NodeBranchEntry nodeBranchEntry : result )
            {
                if ( nodeIndex % logStep == 0 )
                {
                    progressReporter.progress( Math.toIntExact( nodeIndex ), Math.toIntExact( total ) );
                }

                final InternalContext context = InternalContext.create( ContextAccessor.current() ).
                    repositoryId( repositoryId ).
                    branch( branch ).
                    build();

                final NodeVersion nodeVersion = this.nodeVersionService.get( nodeBranchEntry.getVersionId(), context );

                final Node node = NodeFactory.create( nodeVersion, nodeBranchEntry );

                this.indexDataService.store( node, context );

                builder.add( node.id() );

                nodeIndex++;
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

        private ProgressReporter progressReporter;

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

        public Builder progressReporter( final ProgressReporter val )
        {
            progressReporter = val;
            return this;
        }

        public ReindexExecutor build()
        {
            return new ReindexExecutor( this );
        }
    }
}
