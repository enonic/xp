package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.repo.internal.index.IndexContext;

public class GetNodesByPathsCommand
    extends AbstractNodeCommand
{
    private final NodePaths paths;

    private final boolean resolveHasChild;

    private final OrderExpressions orderExpressions;

    private GetNodesByPathsCommand( Builder builder )
    {
        super( builder );
        this.paths = builder.paths;
        this.resolveHasChild = builder.resolveHasChild;
        this.orderExpressions = builder.orderExpressions;
    }

    public Nodes execute()
    {
        final NodeVersionIds versionIds = this.queryService.find( paths, orderExpressions, IndexContext.from( ContextAccessor.current() ) );

        return resolveHasChild ? NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) ) : nodeDao.getByVersionIds( versionIds );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePaths paths;

        private boolean resolveHasChild = true;

        private OrderExpressions orderExpressions = DEFAULT_ORDER_EXPRESSIONS;

        private Builder()
        {
        }

        public Builder paths( NodePaths paths )
        {
            this.paths = paths;
            return this;
        }

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public Builder orderExpressions( final OrderExpressions orderExpressions )
        {
            this.orderExpressions = orderExpressions;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.paths );
        }

        public GetNodesByPathsCommand build()
        {
            this.validate();
            return new GetNodesByPathsCommand( this );
        }
    }
}
