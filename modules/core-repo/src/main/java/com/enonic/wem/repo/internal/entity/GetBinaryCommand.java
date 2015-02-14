package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.core.blob.BlobKey;
import com.enonic.xp.core.data.PropertyPath;
import com.enonic.xp.core.node.AttachedBinary;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeId;
import com.enonic.xp.core.util.BinaryReference;
import com.enonic.wem.repo.internal.blob.BlobStore;

public class GetBinaryCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final BlobStore binaryBlobStore;

    private final PropertyPath propertyPath;

    private final NodeId nodeId;

    private GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.nodeId = builder.nodeId;
        this.binaryBlobStore = builder.binaryBlobStore;
    }

    public ByteSource execute()
    {
        final Node node = doGetById( this.nodeId, false );

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
        final AttachedBinary attachedBinary = node.getAttachedBinaries().getByBinaryReference( this.binaryReference );

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
        final BlobKey blobKey = attachedBinary.getBlobKey();

        return this.binaryBlobStore.getByteSource( blobKey );
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

        private BlobStore binaryBlobStore;

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

        public Builder binaryBlobStore( final BlobStore blobStore )
        {
            this.binaryBlobStore = blobStore;
            return this;
        }

        void validate()
        {
            super.validate();

            Preconditions.checkNotNull( binaryBlobStore, "binaryBlobStore not set" );
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
