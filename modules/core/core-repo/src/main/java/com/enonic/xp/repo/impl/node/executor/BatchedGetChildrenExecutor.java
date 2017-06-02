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

    private Filters filters;

    private ChildOrder childOrder;

    private boolean recursive;

    private BatchedGetChildrenExecutor( final Builder builder )
    {
        this.batchSize = builder.batchSize;
        this.parentId = builder.parentId;
        this.nodeService = builder.nodeService;
        this.filters = builder.filters;
        this.childOrder = builder.childOrder;
        this.recursive = builder.recursive;
    }

    public NodeIds execute()
    {
        final FindNodesByParentParams queryParams = createQuery();

        final FindNodesByParentResult result = this.nodeService.findByParent( queryParams );

        if ( result.isEmpty() )
        {
            hasMore = false;
        }
        else
        {
            currentFrom += batchSize;
            hasMore = true;
        }

        return result.getNodeIds();
    }

    private FindNodesByParentParams createQuery()
    {
        final FindNodesByParentParams.Builder queryBuilder = FindNodesByParentParams.create().
            from( this.currentFrom ).
            size( this.batchSize ).
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
