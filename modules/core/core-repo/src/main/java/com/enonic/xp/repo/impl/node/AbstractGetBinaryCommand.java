package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.util.BinaryReference;

abstract class AbstractGetBinaryCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final BinaryService binaryService;

    AbstractGetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.binaryService = builder.binaryService;
    }

    ByteSource getByBinaryReference( final AttachedBinaries attachedBinaries )
    {
        if ( attachedBinaries == null )
        {
            return null;
        }

        final AttachedBinary attachedBinary = attachedBinaries.getByBinaryReference( this.binaryReference );

        if ( attachedBinary == null )
        {
            return null;
        }

        return this.binaryService.get( ContextAccessor.current().getRepositoryId(), attachedBinary );
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private BinaryReference binaryReference;

        private BinaryService binaryService;

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

        @Override
        void validate() {
            Objects.requireNonNull( binaryService );
            Objects.requireNonNull( binaryReference, "binaryReference is required" );
        }
    }
}
