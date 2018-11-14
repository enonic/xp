package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;

abstract class AbstractGetBinaryCommand
    extends AbstractNodeCommand
{
    protected final BinaryReference binaryReference;

    protected final BinaryService binaryService;

    private final PropertyPath propertyPath;

    AbstractGetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.binaryService = builder.binaryService;
    }

    ByteSource getByBinaryReference( final Node node )
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

    ByteSource getByPropertyPath( final Node node )
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
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return this.binaryService.get( repositoryId, attachedBinary );
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private BinaryReference binaryReference;

        private BinaryService binaryService;

        private PropertyPath propertyPath;

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B binaryReference( final BinaryReference val )
        {
            binaryReference = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B binaryService( final BinaryService val )
        {
            binaryService = val;
            return (B) this;
        }

        void validate() {
            Preconditions.checkNotNull( binaryService, "binaryBlobStore not set" );
            Preconditions.checkArgument( propertyPath != null || binaryReference != null,
                                         "Either propertyPath or binaryReference must be set" );
        }

        @SuppressWarnings("unchecked")
        B propertyPath( final PropertyPath val )
        {
            propertyPath = val;
            return (B) this;
        }

    }
}
