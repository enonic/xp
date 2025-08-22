package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryKeyCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final NodeId nodeId;

    private GetBinaryKeyCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.nodeId = builder.nodeId;
    }

    public String execute()
    {
        final Node node = doGetById( this.nodeId );
        final AttachedBinary attachedBinary = node.getAttachedBinaries().getByBinaryReference( this.binaryReference );

        if ( attachedBinary == null )
        {
            return null;
        }

        return attachedBinary.getBlobKey();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private BinaryReference binaryReference;

        private NodeId nodeId;

        public Builder binaryReference( final BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
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

            Objects.requireNonNull( this.nodeId, "nodeId is required" );
            Preconditions.checkArgument( binaryReference != null, "binaryReference must be set" );
        }

        public GetBinaryKeyCommand build()
        {
            this.validate();
            return new GetBinaryKeyCommand( this );
        }
    }
}
