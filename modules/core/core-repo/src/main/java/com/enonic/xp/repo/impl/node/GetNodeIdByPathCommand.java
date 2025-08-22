package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodeIdByPathCommand
    extends AbstractNodeCommand
{
    private final NodePath nodePath;

    private GetNodeIdByPathCommand( Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeId execute()
    {
        final NodeBranchEntry nodeBranchEntry =
            this.nodeStorageService.getBranchNodeVersion( nodePath, InternalContext.from( ContextAccessor.current() ) );

        return nodeBranchEntry != null ? nodeBranchEntry.getNodeId() : null;
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }


        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( this.nodePath, "nodePath is required" );
        }

        public GetNodeIdByPathCommand build()
        {
            validate();
            return new GetNodeIdByPathCommand( this );
        }
    }
}
