package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final BlobStore binaryBlobStore;

    private final PropertyPath propertyPath;

    private final Node node;

    private GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.node = builder.node;
        this.binaryBlobStore = builder.binaryBlobStore;
    }

    public ByteSource execute()
    {
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
        final BlobKey blobKey = new BlobKey( attachedBinary.getBlobKey() );
        final BlobRecord record = this.binaryBlobStore.getRecord( NodeConstants.BINARY_SEGMENT, blobKey );
        return record != null ? record.getBytes() : null;
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

        private Node node;

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

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder binaryBlobStore( final BlobStore blobStore )
        {
            this.binaryBlobStore = blobStore;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();

            Preconditions.checkNotNull( binaryBlobStore, "binaryBlobStore not set" );
            Preconditions.checkNotNull( node, "node not set" );
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
