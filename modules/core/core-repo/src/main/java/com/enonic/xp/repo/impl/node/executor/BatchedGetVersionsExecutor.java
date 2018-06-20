package com.enonic.xp.repo.impl.node.executor;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;

public class BatchedGetVersionsExecutor
{
    private final int batchSize;

    private final NodeService nodeService;

    private int currentFrom = 0;

    private boolean hasMore = true;

    private final long totalHits;

    private final NodeVersionQuery query;

    private BatchedGetVersionsExecutor( final Builder builder )
    {
        batchSize = builder.batchSize;
        nodeService = builder.nodeService;
        query = builder.query;
        this.totalHits = initTotalHits();
    }

    private long initTotalHits()
    {
        final NodeVersionQueryResult result = this.nodeService.findVersions( NodeVersionQuery.create( this.query ).
            from( 0 ).
            size( 0 ).
            build() );

        return result.getTotalHits();
    }

    public NodeVersionsMetadata execute()
    {
        final NodeVersionQueryResult result = this.nodeService.findVersions( NodeVersionQuery.create( this.query ).
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
        return result.getNodeVersionsMetadata();
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

        private NodeVersionQuery query;

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

        public Builder query( final NodeVersionQuery val )
        {
            query = val;
            return this;
        }

        public BatchedGetVersionsExecutor build()
        {
            return new BatchedGetVersionsExecutor( this );
        }
    }
}
