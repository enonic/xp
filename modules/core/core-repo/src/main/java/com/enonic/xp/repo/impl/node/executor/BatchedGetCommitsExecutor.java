package com.enonic.xp.repo.impl.node.executor;

import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.node.NodeService;

public class BatchedGetCommitsExecutor
{
    private final int batchSize;

    private final NodeService nodeService;

    private int currentFrom = 0;

    private boolean hasMore = true;

    private final long totalHits;

    private final NodeCommitQuery query;

    private BatchedGetCommitsExecutor( final Builder builder )
    {
        batchSize = builder.batchSize;
        nodeService = builder.nodeService;
        query = builder.query;
        this.totalHits = initTotalHits();
    }

    private long initTotalHits()
    {
        final NodeCommitQueryResult result = this.nodeService.findCommits( NodeCommitQuery.create( this.query ).
            from( 0 ).
            size( 0 ).
            build() );

        return result.getTotalHits();
    }

    public NodeCommitEntries execute()
    {
        final NodeCommitQueryResult result = this.nodeService.findCommits( NodeCommitQuery.create( this.query ).
            from( currentFrom ).
            size( batchSize ).
            build() );

        if ( result.getHits() == 0 )
        {
            this.hasMore = false;
        }
        else
        {
            this.currentFrom += this.batchSize;

            this.hasMore = currentFrom < result.getTotalHits();
        }
        return result.getNodeCommitEntries();
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public boolean hasMore()
    {
        return this.hasMore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private int batchSize = 1000;

        private NodeService nodeService;

        private NodeCommitQuery query;

        private Builder()
        {
        }

        public Builder batchSize( final int val )
        {
            batchSize = val;
            return this;
        }


        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder query( final NodeCommitQuery val )
        {
            query = val;
            return this;
        }

        public BatchedGetCommitsExecutor build()
        {
            return new BatchedGetCommitsExecutor( this );
        }
    }
}
