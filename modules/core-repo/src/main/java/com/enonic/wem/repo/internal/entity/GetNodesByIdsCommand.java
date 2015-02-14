package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.wem.repo.internal.index.IndexContext;

public class GetNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final boolean resolveHasChild;

    private final OrderExpressions orderExpressions;

    private GetNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids;
        this.resolveHasChild = builder.resolveHasChild;
        this.orderExpressions = builder.orderExpressions;
    }

    public Nodes execute()
    {
        final NodeVersionIds versionIds =
            this.queryService.find( this.ids, this.orderExpressions, IndexContext.from( ContextAccessor.current() ) );

        final Nodes nodes = nodeDao.getByVersionIds( versionIds );

        return resolveHasChild ? NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( nodes ) : nodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private boolean resolveHasChild = true;

        private OrderExpressions orderExpressions = DEFAULT_ORDER_EXPRESSIONS;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder ids( NodeIds ids )
        {
            this.ids = ids;
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
            Preconditions.checkNotNull( this.ids );
        }

        public GetNodesByIdsCommand build()
        {
            this.validate();
            return new GetNodesByIdsCommand( this );
        }
    }
}
