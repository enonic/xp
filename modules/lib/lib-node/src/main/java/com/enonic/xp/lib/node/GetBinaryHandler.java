package com.enonic.xp.lib.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final BinaryReference binaryReference;

    private GetBinaryHandler( final Builder builder )
    {
        super( builder );
        nodeKey = builder.nodeKey;
        binaryReference = builder.binaryReference;
    }

    public static Builder create()
    {
        return new Builder();
    }


    @Override
    public ByteSource execute()
    {
        final NodeId nodeId = getNodeId( nodeKey );
        return this.nodeService.getBinary( nodeId, binaryReference );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private BinaryReference binaryReference;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder binaryReference( final String val )
        {
            binaryReference = BinaryReference.from( val );
            return this;
        }

        public GetBinaryHandler build()
        {
            return new GetBinaryHandler( this );
        }
    }
}
