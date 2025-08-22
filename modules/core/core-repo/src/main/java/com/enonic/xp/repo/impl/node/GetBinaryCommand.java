package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;

public class GetBinaryCommand
    extends AbstractGetBinaryCommand
{
    private final NodeId nodeId;

    private GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public ByteSource execute()
    {
        final Node node = doGetById( this.nodeId );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot get binary reference, node with id: " + this.nodeId + " not found" );
        }

        return getByBinaryReference( node.getAttachedBinaries() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractGetBinaryCommand.Builder<Builder>
    {
        private NodeId nodeId;

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( nodeId, "nodeId is required" );
        }

        public GetBinaryCommand build()
        {
            this.validate();
            return new GetBinaryCommand( this );
        }
    }
}
