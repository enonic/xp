package com.enonic.wem.repo.internal.entity;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.util.BinaryReference;

public class GetBinaryCommand
    extends AbstractNodeCommand
{
    private final BinaryReference binaryReference;

    private final PropertyPath propertyPath;

    private final NodeId nodeId;

    public GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.binaryReference = builder.binaryReference;
        this.propertyPath = builder.propertyPath;
        this.nodeId = builder.nodeId;
    }

    public ByteSource execute()
    {
        final Node node = doGetById( this.nodeId, false );

        if ( binaryReference != null )
        {
            // Get binary by id
        }

        return null;
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
            this.nodeId = this.nodeId;
            return this;
        }

        public GetBinaryCommand build()
        {
            return new GetBinaryCommand( this );
        }
    }
}
