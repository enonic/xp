package com.enonic.xp.repo.impl.node.executor;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.filter.Filters;

public class BatchedGetChildrenExecutor
{
    private final int batchSize;

    private final NodeId parentId;

    private final NodeService nodeService;

    private int currentFrom = 0;

    private boolean hasMore = true;

    private final Filters filters;

    private final ChildOrder childOrder;

    private final boolean recursive;

    private final Long totalHits;

    private BatchedGetChildrenExecutor( final Builder builder )
    {
        this.batchSize = builder.batchSize;
        this.parentId = builder.parentId;
        this.nodeService = builder.nodeService;
        this.filters = builder.filters;
        this.childOrder = builder.childOrder;
        this.recursive = builder.recursive;
        this.totalHits = initTotalHits();
    }

    private long initTotalHits()
    {
        final FindNodesByParentParams queryParams = createQuery( 0, 0 );

        final FindNodesByParentResult byParent = this.nodeService.findByParent( queryParams );

        return byParent.getTotalHits();
    }

    public NodeIds execute()
    {
        final FindNodesByParentParams queryParams = createQuery( this.currentFrom, this.batchSize );

        final FindNodesByParentResult result = this.nodeService.findByParent( queryParams );

        if ( result.isEmpty() )
        {
            this.hasMore = false;
        }
        else
        {
            this.currentFrom += this.batchSize;

            this.hasMore = currentFrom < result.getTotalHits();
        }
        return result.getNodeIds();
    }

    private FindNodesByParentParams createQuery( final int from, final int size )
    {
        final FindNodesByParentParams.Builder queryBuilder = FindNodesByParentParams.create().
            from( from ).
            size( size ).
            recursive( this.recursive ).
            parentId( this.parentId );

        if ( this.filters != null )
        {
            queryBuilder.queryFilters( this.filters );
        }

        if ( this.childOrder != null )
        {
            queryBuilder.childOrder( this.childOrder );
        }

        return queryBuilder.
            build();
    }

    public Long getTotalHits()
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

        private NodeId parentId;

        private NodeService nodeService;

        private Filters filters;

        private ChildOrder childOrder;

        private boolean recursive = false;

        private Builder()
        {
        }

        public Builder batchSize( final int val )
        {
            batchSize = val;
            return this;
        }

        public Builder parentId( final NodeId val )
        {
            parentId = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder filters( final Filters val )
        {
            filters = val;
            return this;
        }

        public Builder childOrder( final ChildOrder val )
        {
            childOrder = val;
            return this;
        }

        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        public BatchedGetChildrenExecutor build()
        {
            return new BatchedGetChildrenExecutor( this );
        }
    }
}
