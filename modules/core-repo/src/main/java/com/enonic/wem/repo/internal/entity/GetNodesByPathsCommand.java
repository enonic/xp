package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersions;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.OrderExpressions;

public class GetNodesByPathsCommand
    extends AbstractNodeCommand
{
    private final NodePaths paths;

    private final boolean resolveHasChild;

    private GetNodesByPathsCommand( Builder builder )
    {
        super( builder );
        this.paths = builder.paths;
        this.resolveHasChild = builder.resolveHasChild;
    }

    public Nodes execute()
    {
        final NodeBranchVersions nodeBranchVersions = this.branchService.get( paths, InternalContext.from( ContextAccessor.current() ) );
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        nodeBranchVersions.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final NodeVersionIds nodeVersionIds = builder.build();

        return resolveHasChild ? NodeHasChildResolver.create().
            branchService( this.branchService ).
            build().
            resolve( nodeDao.getByVersionIds( nodeVersionIds ) ) : nodeDao.getByVersionIds( nodeVersionIds );
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

        @Override
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
