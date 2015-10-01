package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

public class GetNodeVersionsCommand
{
    private final static int DEFAULT_SIZE = 10;

    private final NodeId nodeId;

    private final int from;

    private final int size;

    private final SearchService searchService;

    private GetNodeVersionsCommand( Builder builder )
    {
        nodeId = builder.nodeId;
        from = builder.from;
        size = builder.size;
        searchService = builder.searchService;
    }

    public NodeVersionQueryResult execute()
    {
        final NodeVersionQuery query = NodeVersionQuery.create().
            nodeId( this.nodeId ).
            from( this.from ).
            size( this.size ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        return this.searchService.search( query, InternalContext.from( ContextAccessor.current() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private SearchService searchService;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder searchService( final SearchService searchService )
        {
            this.searchService = searchService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.searchService, "SearchService must be set" );
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
        }

        public GetNodeVersionsCommand build()
        {
            this.validate();
            return new GetNodeVersionsCommand( this );
        }
    }
}
