package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryKeyCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final PropertyPath propertyPath;

    private final Node node;

    private GetBinaryKeyCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.node = builder.node;
    }

    public String execute()
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

    private String getByBinaryReference( final Node node )
    {
        final AttachedBinary attachedBinary = node.getAttachedBinaries().getByBinaryReference( this.binaryReference );

        if ( attachedBinary == null )
        {
            return null;
        }

        return doGetBlobKey( attachedBinary );
    }

    private String getByPropertyPath( final Node node )
    {
        final BinaryReference binaryReference = node.data().getBinaryReference( this.propertyPath );

        if ( binaryReference == null )
        {
            return null;
        }

        final AttachedBinary attachedBinary = node.getAttachedBinaries().getByBinaryReference( binaryReference );

        return doGetBlobKey( attachedBinary );
    }

    private String doGetBlobKey( final AttachedBinary attachedBinary )
    {
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

        private PropertyPath propertyPath;

        private Node node;

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

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( node, "node not set" );
            Preconditions.checkArgument( propertyPath != null || binaryReference != null,
                                         "Either propertyPath or binaryReference must be set" );
        }

        public GetBinaryKeyCommand build()
        {
            this.validate();
            return new GetBinaryKeyCommand( this );
        }
    }
}
