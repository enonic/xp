package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetBinaryByVersionCommand
    extends AbstractGetBinaryCommand
{
    private final NodeVersionId nodeVersionId;

    private final NodeId nodeId;

    private GetBinaryByVersionCommand( final Builder builder )
    {
        super( builder );
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeId = builder.nodeId;
    }

    public ByteSource execute()
    {
        final Node node = this.nodeStorageService.get( nodeVersionId, InternalContext.from( ContextAccessor.current() ) );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot get binary reference, node with versionId: " + this.nodeVersionId + " not found" );
        }

        if ( !node.id().equals( nodeId ) )
        {
            throw new NodeNotFoundException( "NodeVersionId [" + nodeVersionId + "] not a version of Node with id [" + nodeId + "]" );
        }

        return getByBinaryReference( node.getAttachedBinaries() );
    }

    public static GetBinaryByVersionCommand.Builder create()
    {
        return new GetBinaryByVersionCommand.Builder();
    }

    public static class Builder
        extends AbstractGetBinaryCommand.Builder<Builder>
    {
        private NodeVersionId nodeVersionId;

        private NodeId nodeId;


        public Builder()
        {
            super();
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( this.nodeVersionId, "nodeVersionId is required" );
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
        }

        public GetBinaryByVersionCommand build()
        {
            this.validate();
            return new GetBinaryByVersionCommand( this );
        }
    }
}
