package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.util.BinaryReference;

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

        if ( binaryReference != null )
        {
            return getByBinaryReference( node );
        }
        else
        {
            return getByPropertyPath( node );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractGetBinaryCommand.Builder<Builder>
    {
        private BinaryReference binaryReference;

        private PropertyPath propertyPath;

        private NodeId nodeId;

        private BinaryService binaryService;

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId, "nodeId not set" );
        }

        public GetBinaryCommand build()
        {
            this.validate();
            return new GetBinaryCommand( this );
        }
    }
}
