package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;

public class GetOutgoingReferencesCommand
    extends AbstractNodeCommand
{
    private NodeId nodeId;

    private GetOutgoingReferencesCommand( Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
    }

    public NodeIds execute()
    {
        final NodeBranchMetadata branchNodeVersion =
            this.storageService.getBranchNodeVersion( nodeId, InternalContext.from( ContextAccessor.current() ) );

        return branchNodeVersion.getReferences();
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
        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder( AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
            super.validate();
        }

        public GetOutgoingReferencesCommand build()
        {
            return new GetOutgoingReferencesCommand( this );
        }
    }
}
