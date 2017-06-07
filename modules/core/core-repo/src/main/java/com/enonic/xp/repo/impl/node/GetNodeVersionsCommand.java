package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQueryResultFactory;

public class GetNodeVersionsCommand
{
    private final static int DEFAULT_SIZE = 10;

    private final NodeId nodeId;

    private final int from;

    private final int size;

    private final NodeSearchService nodeSearchService;

    private GetNodeVersionsCommand( Builder builder )
    {
        nodeId = builder.nodeId;
        from = builder.from;
        size = builder.size;
        nodeSearchService = builder.nodeSearchService;
    }

    public NodeVersionQueryResult execute()
    {
        final NodeVersionQuery query = NodeVersionQuery.create().
            nodeId( this.nodeId ).
            from( this.from ).
            size( this.size ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        final SearchResult result = this.nodeSearchService.query( query, SingleRepoStorageSource.create(
            ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) );

        if ( result.isEmpty() )
        {
            return NodeVersionQueryResult.empty( result.getTotalHits() );
        }

        return NodeVersionQueryResultFactory.create( query, result );
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

        private NodeSearchService nodeSearchService;

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

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeSearchService, "SearchService must be set" );
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
        }

        public GetNodeVersionsCommand build()
        {
            this.validate();
            return new GetNodeVersionsCommand( this );
        }
    }
}
