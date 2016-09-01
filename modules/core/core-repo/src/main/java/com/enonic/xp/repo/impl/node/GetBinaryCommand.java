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
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final BinaryService binaryService;

    private final PropertyPath propertyPath;

    private final NodeId nodeId;

    private GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.nodeId = builder.nodeId;
        this.binaryService = builder.binaryService;
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

    private ByteSource getByBinaryReference( final Node node )
    {
        final AttachedBinaries attachedBinaries = node.getAttachedBinaries();

        if ( attachedBinaries == null )
        {
            return null;
        }

        final AttachedBinary attachedBinary = attachedBinaries.getByBinaryReference( this.binaryReference );

        if ( attachedBinary == null )
        {
            return null;
        }

        return doGetByteSource( attachedBinary );
    }

    private ByteSource getByPropertyPath( final Node node )
    {
        final BinaryReference binaryReference = node.data().getBinaryReference( this.propertyPath );

        if ( binaryReference == null )
        {
            return null;
        }

        final AttachedBinary attachedBinary = node.getAttachedBinaries().getByBinaryReference( binaryReference );

        return doGetByteSource( attachedBinary );
    }

    private ByteSource doGetByteSource( final AttachedBinary attachedBinary )
    {
        return this.binaryService.get( attachedBinary );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private BinaryReference binaryReference;

        private PropertyPath propertyPath;

        private NodeId nodeId;

        private BinaryService binaryService;

        public Builder binaryReference( final BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
            return this;
        }

        public Builder propertyPath( final PropertyPath propertyPath )
        {
            this.propertyPath = propertyPath;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();

            Preconditions.checkNotNull( binaryService, "binaryBlobStore not set" );
            Preconditions.checkNotNull( nodeId, "nodeId not set" );

            Preconditions.checkArgument( propertyPath != null || binaryReference != null,
                                         "Either propertyPath or binaryReference must be set" );
        }

        public GetBinaryCommand build()
        {
            this.validate();
            return new GetBinaryCommand( this );
        }
    }
}
