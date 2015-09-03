package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersions;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;

public class GetNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final boolean resolveHasChild;

    private GetNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids;
        this.resolveHasChild = builder.resolveHasChild;
    }

    public Nodes execute()
    {
        final NodeBranchVersions nodeBranchVersions = this.branchService.get( ids, InternalContext.from( ContextAccessor.current() ) );
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        nodeBranchVersions.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final Nodes nodes = nodeDao.getByVersionIds( builder.build() );

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

        @Override
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
