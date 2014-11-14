package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.repo.NodeIds;
import com.enonic.wem.repo.NodeVersionIds;
import com.enonic.wem.repo.Nodes;

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

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private boolean resolveHasChild;

        private OrderExpressions orderExpressions = DEFAULT_ORDER_EXPRESSIONS;

        private Builder()
        {
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

        public GetNodesByIdsCommand build()
        {
            return new GetNodesByIdsCommand( this );
        }
    }
}
