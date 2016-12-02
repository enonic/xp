package com.enonic.xp.lib.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;

public class GetBinaryHandler
    extends BaseNodeHandler
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


    public ByteSource execute()
    {
        final NodeId nodeId;

        if ( nodeKey.isId() )
        {
            nodeId = nodeKey.getAsNodeId();
        }
        else
        {
            final Node node = this.nodeService.getByPath( nodeKey.getAsPath() );
            if ( node == null )
            {
                return null;
            }

            nodeId = node.id();
        }

        return this.nodeService.getBinary( nodeId, binaryReference );
    }

    public static final class Builder
        extends BaseNodeHandler.Builder<Builder>
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
