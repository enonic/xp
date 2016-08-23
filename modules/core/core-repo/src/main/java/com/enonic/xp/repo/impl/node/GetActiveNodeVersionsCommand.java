package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.branch.BranchIds;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;

public class GetActiveNodeVersionsCommand
    extends AbstractNodeCommand
{
    private final BranchIds branchIds;

    private final NodeId nodeId;

    private GetActiveNodeVersionsCommand( final Builder builder )
    {
        super( builder );
        this.branchIds = builder.branchIds;
        this.nodeId = builder.nodeId;
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveNodeVersionsResult execute()
    {
        final GetActiveNodeVersionsResult.Builder builder = GetActiveNodeVersionsResult.create();

        for ( final BranchId branchId : branchIds )
        {
            final Context context = ContextAccessor.current();

            final NodeBranchEntry nodeBranchEntry =
                this.storageService.getBranchNodeVersion( this.nodeId, InternalContext.create( context ).
                    branch( branchId ).
                    build() );

            if ( nodeBranchEntry != null )
            {
                builder.add( branchId, this.storageService.getVersion(
                    new NodeVersionDocumentId( nodeBranchEntry.getNodeId(), nodeBranchEntry.getVersionId() ),
                    InternalContext.from( context ) ) );
            }
        }
        return builder.build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private BranchIds branchIds;

        private NodeId nodeId;

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder()
        {
        }

        public Builder branches( final BranchIds branchIds )
        {
            this.branchIds = branchIds;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( this.nodeId );
            Preconditions.checkNotNull( this.branchIds );
        }

        public GetActiveNodeVersionsCommand build()
        {
            this.validate();
            return new GetActiveNodeVersionsCommand( this );
        }
    }
}
